package menghu.chat.feature.chat.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import menghu.chat.feature.chat.model.ConversationEntity
import menghu.chat.feature.chat.model.MessageEntity
import menghu.chat.feature.chat.repository.ChatRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 单聊页面 ViewModel
 * - 路由参数 conversationId 采用「双路径加载」策略：
 *   1) 优先从 SavedStateHandle 读取（Hilt 自动注入的导航参数）
 *   2) 如果 SavedStateHandle 读取失败（类型不匹配等），ChatScreen 会显式调用
 *      ensureConversationId() 传入兜底 ID，触发异步加载
 * - 订阅 Room 的消息 Flow，UI 随数据库变更自动刷新
 * - 提供输入文本、发送消息、模拟对方回复等能力
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    /** 当前会话 ID：MutableStateFlow，可在 UI 层 fallback 时更新
     *  ⚠️ 之前的版本用 get<String>() 读取 NavType.LongType 参数会抛 ClassCastException
     *  这里显式先尝试 get<Long>，失败再降级为 get<String>，再失败则为 0
     */
    private val _conversationId = MutableStateFlow(
        parseConversationId(savedStateHandle)
    )

    /** 当前会话（含昵称、头像、是否在线）
     */
    private val _conversation = MutableStateFlow<ConversationEntity?>(null)
    val conversation: StateFlow<ConversationEntity?> = _conversation.asStateFlow()

    /** 消息列表 Flow
     */
    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages.asStateFlow()

    /** 输入框文本（由输入框绑定）
     */
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    /** 对方是否正在输入（演示模拟用）
     */
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    /** 当前正在进行的加载 Job，用于防止重复订阅 Flow
     */
    private var loadJob: Job? = null

    init {
        val id = _conversationId.value
        if (id > 0) {
            Timber.d("[ChatViewModel] init，SavedStateHandle 解析成功，conversationId = %s，启动加载", id)
            startLoad(id)
        } else {
            Timber.w("[ChatViewModel] init，SavedStateHandle 未拿到有效 conversationId，等待 UI 层 fallback 调用 ensureConversationId()")
        }
    }

    /** UI 层兜底入口：当 SavedStateHandle 未能解析出 ID 时，由 ChatScreen 显式传入
     *  如果当前已有相同 ID 的加载在进行，则忽略；否则取消旧的并重新加载
     */
    fun ensureConversationId(id: Long) {
        if (id <= 0) {
            Timber.w("[ChatViewModel] ensureConversationId 收到无效 id=%s，忽略", id)
            return
        }
        val current = _conversationId.value
        if (current == id && _conversation.value != null) {
            // 已在加载或加载完成，无需重复
            return
        }
        Timber.d("[ChatViewModel] ensureConversationId: %s → %s", current, id)
        _conversationId.value = id
        startLoad(id)
    }

    /** 从 SavedStateHandle 中解析 conversationId
     *  ⚠️【关键修复】SavedStateHandle.get<T>() 内部实现为 `return map[key] as T?`，
     *  如果 Bundle 里存的是 Long（NavType.LongType），调用 get<String>() 会触发
     *  `java.lang.Long cannot be cast to java.lang.String` 的 ClassCastException。
     *
     *  正确做法：先以 `get<Any?>` 获取原始值，再根据实际运行时类型判断并转换。
     */
    private fun parseConversationId(handle: SavedStateHandle): Long {
        return try {
            val raw: Any? = handle.get<Any?>("conversationId")
            val id = when (raw) {
                null -> {
                    Timber.w("[ChatViewModel] SavedStateHandle 中无 conversationId 参数")
                    0L
                }
                is Long -> raw
                is Int -> raw.toLong()
                is String -> raw.toLongOrNull() ?: 0L
                else -> {
                    Timber.w("[ChatViewModel] conversationId 参数类型异常: %s (%s)", raw, raw.javaClass.name)
                    0L
                }
            }
            Timber.d("[ChatViewModel] SavedStateHandle 解析 conversationId = %s (raw=%s)", id, raw)
            id
        } catch (ex: Throwable) {
            Timber.e(ex, "[ChatViewModel] SavedStateHandle 解析 conversationId 失败，回退为 0")
            0L
        }
    }

    /** 启动加载流程：获取会话实体 + 插入默认消息（如为空） + 订阅消息 Flow
     *  会先取消之前的 Job，确保不会重复订阅同一个 Flow
     */
    private fun startLoad(id: Long) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            try {
                // 1) 加载会话实体
                val conv = chatRepository.getConversationById(id)
                if (conv != null) {
                    Timber.d("[ChatViewModel] 从数据库加载到会话: %s", conv.peerName)
                    _conversation.value = conv
                } else {
                    Timber.w("[ChatViewModel] 数据库中未找到会话 %s，构造默认会话", id)
                    _conversation.value = ConversationEntity(
                        id = id,
                        peerId = id + 100L,
                        peerName = "Chat Partner",
                        peerAvatar = "",
                        lastMessage = "",
                        lastMessageAt = System.currentTimeMillis(),
                        unreadCount = 0,
                        isPinned = false,
                        peerOnline = true
                    )
                }

                // 2) 首次进入若消息为空，插入 4 条示例对话
                val current = chatRepository.getMessages(id)
                if (current.isEmpty()) {
                    Timber.d("[ChatViewModel] 会话 %s 无消息，插入默认对话", id)
                    insertDefaultMessages(id, _conversation.value)
                }

                // 3) 订阅消息 Flow（数据库变动时自动更新 UI，直到 ViewModel 销毁）
                chatRepository.observeMessages(id).collect { list ->
                    Timber.d("[ChatViewModel] 收到消息列表更新，共 %d 条", list.size)
                    _messages.value = list
                }
            } catch (th: Throwable) {
                Timber.e(th, "[ChatViewModel] 加载会话消息失败")
            }
        }
    }

    /** 首次进入插入默认消息（4 条示范对话）
     */
    private suspend fun insertDefaultMessages(id: Long, conv: ConversationEntity?) {
        val peerId = conv?.peerId ?: id + 100L
        val peerName = conv?.peerName ?: "Chat Partner"
        val shortName = peerName.split(" ").firstOrNull() ?: "there"
        val base = System.currentTimeMillis()
        val sequence = listOf(
            MessageStub(senderId = peerId, senderName = peerName, content = "Hello, $shortName!"),
            MessageStub(senderId = 0L, senderName = "我", content = "Hi $shortName, how are you?"),
            MessageStub(senderId = peerId, senderName = peerName, content = "I'm doing great, thanks for asking!"),
            MessageStub(senderId = 0L, senderName = "我", content = "That's awesome to hear 😊")
        )
        sequence.forEachIndexed { index, stub ->
            chatRepository.savePeerMessage(
                conversationId = id,
                senderId = stub.senderId,
                senderName = stub.senderName,
                content = stub.content,
                createdAt = base - (sequence.size - index) * 60_000L
            )
        }
    }

    /** 本地数据类：仅用于批量构造默认消息
     */
    private data class MessageStub(
        val senderId: Long,
        val senderName: String,
        val content: String
    )

    /** UI 回调：更新输入文本
     */
    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    /** UI 回调：点击发送按钮
     */
    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) {
            Timber.d("[ChatViewModel] 发送文本为空，忽略")
            return
        }
        val id = _conversationId.value
        if (id <= 0) {
            Timber.w("[ChatViewModel] sendMessage 时 conversationId=%s，无效", id)
            return
        }
        viewModelScope.launch {
            Timber.d("[ChatViewModel] 发送消息: %s", text.take(30))
            chatRepository.sendTextMessage(
                conversationId = id,
                content = text,
                senderName = "我"
            )
            _inputText.value = ""
            // 1 秒后模拟对方回复
            simulatePeerReply()
        }
    }

    /** 模拟对方回复：短暂显示"正在输入"气泡 → 插入一条对方消息
     */
    private fun simulatePeerReply() {
        viewModelScope.launch {
            _isTyping.value = true
            kotlinx.coroutines.delay(1_000)
            _isTyping.value = false
            val conv = _conversation.value ?: return@launch
            val id = _conversationId.value
            val peerId = conv.peerId
            val peerName = conv.peerName
            val replies = listOf(
                "Got it, thanks!",
                "Sounds good 👍",
                "I'll get back to you soon",
                "OK, talk later",
                "Understood"
            )
            val reply = replies.random()
            Timber.d("[ChatViewModel] 模拟对方回复: %s", reply)
            chatRepository.savePeerMessage(
                conversationId = id,
                senderId = peerId,
                senderName = peerName,
                content = reply,
                createdAt = System.currentTimeMillis()
            )
        }
    }
}
