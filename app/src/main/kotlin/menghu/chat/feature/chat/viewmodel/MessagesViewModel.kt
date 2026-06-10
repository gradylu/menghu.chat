package menghu.chat.feature.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import menghu.chat.feature.chat.model.ConversationEntity
import menghu.chat.feature.chat.repository.ChatRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 会话列表 ViewModel
 * - 负责加载会话列表、在首次无数据时触发 Mock 数据生成
 * - 暴露可观察状态（会话列表、加载状态、错误信息）供 UI 订阅
 */
@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    /** 内部可变：会话列表流（来自 Room Flow，自动响应数据库变更）
     */
    private val _conversations = MutableStateFlow<List<ConversationEntity>>(emptyList())
    val conversations: StateFlow<List<ConversationEntity>> = _conversations.asStateFlow()

    /** 是否正在加载（首次进入会触发 Mock 插入）
     */
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** 错误信息（为空表示无错误）
     */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** 搜索关键字（用于 UI 过滤显示的会话，默认空串表示不过滤）
     */
    private val _keyword = MutableStateFlow("")
    val keyword: StateFlow<String> = _keyword.asStateFlow()

    init {
        Timber.d("[MessagesViewModel] init，开始加载会话列表")
        loadConversations()
    }

    /** 启动数据加载流程：订阅 Flow + 首次无数据时插入 Mock
     *  - 注意：Room Flow 是无限流，collect 永不返回，所以加载状态不可放在 finally
     *  - 正确做法：在 Flow 首次发射后立即解除 Loading
     */
    private fun loadConversations() {
        viewModelScope.launch {
            try {
                // 1) 若本地无数据，则先插入 Mock 数据（保证 UI 首屏可见）
                val inserted = chatRepository.insertMockIfEmpty()
                if (inserted > 0) {
                    Timber.d("[MessagesViewModel] 已插入 %d 条 Mock 会话", inserted)
                }

                // 2) 订阅 Room Flow（后续数据库有变动时，UI 自动刷新）
                //    ⚠️ Room Flow 永不完成，collect 永不返回，加载状态必须在首次发射后解除
                chatRepository.observeConversations().collect { list ->
                    Timber.d("[MessagesViewModel] 收到会话列表更新，共 %d 条", list.size)
                    _conversations.value = list
                    // 首次拿到数据即解除 Loading，后续数据库变更不再重新进入加载态
                    if (_isLoading.value) {
                        _isLoading.value = false
                    }
                }
            } catch (th: Throwable) {
                Timber.e(th, "[MessagesViewModel] 加载会话列表失败")
                _error.value = th.message ?: "未知错误"
                _isLoading.value = false
            }
        }
    }

    /** UI 层回调：更新搜索关键字（ViewModel 维护状态，UI 不做本地业务）
     */
    fun onKeywordChanged(keyword: String) {
        _keyword.value = keyword
    }
}
