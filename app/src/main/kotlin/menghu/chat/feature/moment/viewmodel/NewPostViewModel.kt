package menghu.chat.feature.moment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import menghu.chat.feature.moment.repository.MomentRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 发布动态第 2 步 ViewModel
 * 负责保存用户输入的正文（图片已由前一步决定），并提交至 Room
 */
@HiltViewModel
class NewPostViewModel @Inject constructor(
    private val repository: MomentRepository
) : ViewModel() {

    // 当前正在编辑的图片列表（由前一步传入）
    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>> = _images.asStateFlow()

    // 正在发布状态
    private val _isPublishing = MutableStateFlow(false)
    val isPublishing: StateFlow<Boolean> = _isPublishing.asStateFlow()

    // 发布成功回调（由 UI 层根据其变化触发导航返回）
    private val _publishSuccess = MutableStateFlow(false)
    val publishSuccess: StateFlow<Boolean> = _publishSuccess.asStateFlow()

    // 最新一条错误提示
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * 设置预选图片列表（页面进入时调用一次）
     */
    fun setSelectedImages(images: List<String>) {
        Timber.d("[NewPostViewModel] 设置预选图片 %d 张", images.size)
        _images.value = images
    }

    /**
     * 追加单张图片
     */
    fun appendImage(url: String) {
        if (url.isBlank()) return
        _images.value = _images.value + url
    }

    /**
     * 发布一条新动态
     * @param content 正文文本
     */
    fun publishPost(content: String) {
        if (_isPublishing.value) return
        _isPublishing.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                Timber.d("[NewPostViewModel] 发布中，正文长度=%d，图片=%d",
                    content.length, _images.value.size)
                repository.publishPost(
                    content = content,
                    images = _images.value,
                    authorName = "我"
                )
                _publishSuccess.value = true
            } catch (t: Throwable) {
                Timber.e(t, "[NewPostViewModel] 发布失败")
                _error.value = t.message ?: "发布失败，请稍后重试"
            } finally {
                _isPublishing.value = false
            }
        }
    }

    /**
     * 重置成功标志（导航完成后调用）
     */
    fun resetPublishSuccess() {
        _publishSuccess.value = false
    }
}
