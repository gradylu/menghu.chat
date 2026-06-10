package menghu.chat.feature.moment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import menghu.chat.feature.moment.model.CommentEntity
import menghu.chat.feature.moment.repository.MomentRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 评论页 ViewModel
 * 负责根据 postId 拉取评论列表、发送新评论、处理加载/错误状态
 */
@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val repository: MomentRepository
) : ViewModel() {

    // 评论列表（订阅 Flow）
    private val _comments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val comments: StateFlow<List<CommentEntity>> = _comments.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误信息
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // 当前动态 ID（loadComments 调用后设置）
    private var currentPostId: Long = 0L

    /**
     * 根据 postId 订阅评论列表
     */
    fun loadComments(postId: Long) {
        if (postId <= 0) return
        currentPostId = postId
        _isLoading.value = true
        _error.value = null
        Timber.d("[CommentsViewModel] 加载 postId=%d 的评论", postId)

        viewModelScope.launch {
            try {
                repository.observeComments(postId).collect { list ->
                    Timber.d("[CommentsViewModel] 收到评论列表 %d 条", list.size)
                    _comments.value = list
                    _isLoading.value = false
                }
            } catch (t: Throwable) {
                Timber.e(t, "[CommentsViewModel] 加载评论失败")
                _error.value = t.message ?: "加载失败"
                _isLoading.value = false
            }
        }
    }

    /**
     * 发送评论
     */
    fun sendComment(text: String) {
        if (text.isBlank() || currentPostId <= 0) return
        viewModelScope.launch {
            Timber.d("[CommentsViewModel] 发送评论：%s", text.take(30))
            repository.sendComment(
                postId = currentPostId,
                content = text,
                authorName = "我"
            )
        }
    }
}
