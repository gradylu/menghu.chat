package menghu.chat.feature.chat.repository

import kotlinx.coroutines.flow.Flow
import menghu.chat.feature.chat.datasource.ChatLocalDataSource
import menghu.chat.feature.chat.model.ConversationEntity
import menghu.chat.feature.chat.model.MessageEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 聊天仓库层
 * 提供会话列表、消息列表、发送消息、Mock 数据插入等业务能力
 * - 数据来源：本地 Room 数据库（ChatLocalDataSource）
 */
@Singleton
class ChatRepository @Inject constructor(
    private val localDataSource: ChatLocalDataSource
) {

    /** 观察会话列表（Flow 自动更新）
     */
    fun observeConversations(): Flow<List<ConversationEntity>> =
        localDataSource.observeConversations()

    /** 同步获取会话列表
     */
    suspend fun getConversations(): List<ConversationEntity> =
        localDataSource.getConversations()

    /** 观察指定会话下的所有消息（Flow 自动更新）
     */
    fun observeMessages(conversationId: Long): Flow<List<MessageEntity>> =
        localDataSource.observeMessages(conversationId)

    /** 同步获取指定会话的消息列表
     */
    suspend fun getMessages(conversationId: Long): List<MessageEntity> =
        localDataSource.getMessages(conversationId)

    /** 根据 ID 加载会话实体
     */
    suspend fun getConversationById(id: Long): ConversationEntity? =
        localDataSource.getConversationById(id)

    /** 发送一条文本消息（写入 Room；同时更新会话预览与时间戳
     * @param conversationId 会话 ID
     * @param content 消息内容
     * @param senderName 发送者显示名称（自己发送时填"我"）
     */
    suspend fun sendTextMessage(
        conversationId: Long,
        content: String,
        senderName: String = "我"
    ) {
        Timber.d("[ChatRepository] 发送消息: conversationId=%s, content=%s", conversationId, content.take(30))
        val now = System.currentTimeMillis()
        val message = MessageEntity(
            id = 0,
            conversationId = conversationId,
            senderId = 0, // 0 表示自己
            senderName = senderName,
            type = "text",
            content = content,
            mediaUrl = "",
            isRead = true,
            status = "sent",
            createdAt = now
        )
        localDataSource.saveMessage(message)
        val current = localDataSource.getConversationById(conversationId)
        if (current != null) {
            val updated = current.copy(
                lastMessage = content,
                lastMessageAt = now
            )
            localDataSource.updateConversation(updated)
        }
    }

    /** 保存一条"对方"消息（比如模拟回复时，发送者 senderId ≠ 0）
     * @param conversationId 会话 ID
     * @param senderId 对方用户 ID
     * @param senderName 对方昵称
     * @param content 消息内容
     * @param createdAt 时间戳
     */
    suspend fun savePeerMessage(
        conversationId: Long,
        senderId: Long,
        senderName: String,
        content: String,
        createdAt: Long = System.currentTimeMillis()
    ) {
        Timber.d(
            "[ChatRepository] 保存对方消息: conversationId=%s, senderId=%s, content=%s",
            conversationId, senderId, content.take(30)
        )
        val message = MessageEntity(
            id = 0,
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            type = "text",
            content = content,
            mediaUrl = "",
            isRead = true,
            status = "sent",
            createdAt = createdAt
        )
        localDataSource.saveMessage(message)
        // 同步更新会话预览（让会话列表显示最新一条消息）
        val current = localDataSource.getConversationById(conversationId)
        if (current != null) {
            val updated = current.copy(
                lastMessage = content,
                lastMessageAt = createdAt
            )
            localDataSource.updateConversation(updated)
        }
    }

    /** 如果本地无数据时，插入一整套模拟会话与消息（至少 6 个会话，每会话 3-8 条消息）
     * @return 是否实际插入的会话数量
     */
    suspend fun insertMockIfEmpty(): Int {
        val existing = localDataSource.getConversations()
        if (existing.isNotEmpty()) {
            Timber.d("[ChatRepository] 本地已有会话数据，跳过 Mock 插入")
            return 0
        }
        Timber.d("[ChatRepository] 开始插入 Mock 数据")
        // 1) 插入会话列表
        val now = System.currentTimeMillis()
        val conversations = listOf(
            ConversationEntity(
                id = 1,
                peerId = 101,
                peerName = "Theresa Webb",
                peerAvatar = "",
                lastMessage = "Sure, let's sync up tomorrow",
                lastMessageAt = now - 60_000,
                unreadCount = 2,
                isPinned = true,
                peerOnline = true
            ),
            ConversationEntity(
                id = 2,
                peerId = 102,
                peerName = "Salman Howard",
                peerAvatar = "",
                lastMessage = "I'll send the files in a minute",
                lastMessageAt = now - 300_000,
                unreadCount = 0,
                isPinned = false,
                peerOnline = true
            ),
            ConversationEntity(
                id = 3,
                peerId = 103,
                peerName = "Eleanor Pena",
                peerAvatar = "",
                lastMessage = "Thanks for helping me out!",
                lastMessageAt = now - 2 * 3_600_000,
                unreadCount = 5,
                isPinned = false,
                peerOnline = false
            ),
            ConversationEntity(
                id = 4,
                peerId = 104,
                peerName = "Jenny Wilson",
                peerAvatar = "",
                lastMessage = "See you on Friday 👋",
                lastMessageAt = now - 5 * 3_600_000,
                unreadCount = 0,
                isPinned = false,
                peerOnline = false
            ),
            ConversationEntity(
                id = 5,
                peerId = 105,
                peerName = "Robert Fox",
                peerAvatar = "",
                lastMessage = "Let me check and get back to you",
                lastMessageAt = now - 24 * 3_600_000,
                unreadCount = 1,
                isPinned = false,
                peerOnline = true
            ),
            ConversationEntity(
                id = 6,
                peerId = 106,
                peerName = "Savannah Nguyen",
                peerAvatar = "",
                lastMessage = "Got it, thanks!",
                lastMessageAt = now - 2 * 24 * 3_600_000,
                unreadCount = 0,
                isPinned = false,
                peerOnline = false
            )
        )
        localDataSource.saveConversations(conversations)

        // 2) 为每个会话插入 3-8 条不等的消息（对方与自己交替）
        val allMessages = mutableListOf<MessageEntity>()
        conversations.forEach { conv ->
            val count = (3..8).random().coerceAtLeast(3)
            val baseTime = conv.lastMessageAt
            val texts = buildMockTextsFor(conv.peerId, conv.peerName, count)
            texts.forEachIndexed { index, (senderIsMe, text) ->
                allMessages.add(
                    MessageEntity(
                        id = 0,
                        conversationId = conv.id,
                        senderId = if (senderIsMe) 0 else conv.peerId,
                        senderName = if (senderIsMe) "我" else conv.peerName,
                        type = "text",
                        content = text,
                        mediaUrl = "",
                        isRead = true,
                        status = "sent",
                        createdAt = baseTime - (count - index - 1) * 60_000 - (index + 1) * 30_000
                    )
                )
            }
        }
        localDataSource.saveMessages(allMessages)

        Timber.d(
            "[ChatRepository] Mock 数据插入完成：会话 %d 条，消息 %d 条",
            conversations.size,
            allMessages.size
        )
        return conversations.size
    }

    /** 根据对方 userId 与昵称构造若干会话的模拟对话文本列表
     * 返回：(是否是自己发送，文本内容
     */
    private fun buildMockTextsFor(
        userId: Long,
        peerName: String,
        count: Int
    ): List<Pair<Boolean, String>> {
        // 以对方 ID 为随机种子，保证同一会话每次内容固定
        val random = java.util.Random(userId * 31L + peerName.length)
        // 通用英文模板，首行固定为"Hello, $peerName"
        val pool = listOf(
            "Hello, $peerName" to false,
            "Hello, ${peerName.split(" ").firstOrNull() ?: "friend"} how are you?" to true,
            "I'm doing great, thanks for asking!" to false,
            "That's awesome to hear!" to true,
            "Did you finish the task we talked about?" to false,
            "Yes, almost done — will share shortly" to true,
            "Perfect, looking forward to it" to false,
            "Let me know when you are free" to true,
            "Sure thing, I'll ping you" to false,
            "Thanks a lot 🙏" to true,
            "Anytime, have a nice day!" to false
        )
        // 打乱并取前 count 条（第一条固定"Hello, Theresa"等
        val shuffled = pool.shuffled(random)
        val result = mutableListOf<Pair<Boolean, String>>()
        // 首条固定：对方先发
        result.add(false to "Hello, ${peerName.split(" ").firstOrNull() ?: "there"}")
        // 后续从打乱后的列表中取（不包括重复）
        // pool 元素结构为 Pair<text:String, isMe:Boolean>，这里按真实字段名解构
        for ((text, isMe) in shuffled) {
            if (result.size >= count) break
            if (result.lastOrNull()?.second != text) {
                result.add(isMe to text)
            }
        }
        // 如果还不够，用通用文本补齐
        while (result.size < count) {
            val isMe = result.size % 2 == 1
            val fill = if (isMe) "Got it, thanks!" else "Sounds good to me 👍"
            result.add(isMe to fill)
        }
        return result
    }
}
