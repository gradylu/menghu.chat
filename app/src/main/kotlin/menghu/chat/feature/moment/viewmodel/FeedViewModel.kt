package menghu.chat.feature.moment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import menghu.chat.feature.moment.model.PostEntity
import menghu.chat.feature.moment.model.StoryEntity
import menghu.chat.feature.moment.repository.MomentRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Feed 动态流 ViewModel
 * 负责从 MomentRepository 读取动态/故事数据，并在首次为空时插入 Mock 数据
 * 通过 StateFlow 暴露 UI 状态，完全隔离业务逻辑与 UI 层
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: MomentRepository
) : ViewModel() {

    // 动态列表状态（内部可变，外部只读）
    private val _posts = MutableStateFlow<List<PostEntity>>(emptyList())
    val posts: StateFlow<List<PostEntity>> = _posts.asStateFlow()

    // 故事列表状态
    private val _stories = MutableStateFlow<List<StoryEntity>>(emptyList())
    val stories: StateFlow<List<StoryEntity>> = _stories.asStateFlow()

    // 加载中状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误信息（可为空表示无错误）
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        Timber.i("[FeedViewModel] 初始化，开始加载 Feed 数据")
        loadFeedData()
    }

    /**
     * 加载动态与故事数据：
     * 1. 先尝试插入 Mock 数据（若数据库为空）
     * 2. 通过 Flow 订阅最新列表并写入 StateFlow
     */
    fun loadFeedData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // 若数据库为空，则先插入 Mock 样例
                repository.insertSampleDataIfEmpty()

                // 订阅动态列表流
                launch {
                    repository.observePosts().collect { postList ->
                        Timber.d("[FeedViewModel] 收到动态列表更新，共 %d 条", postList.size)
                        _posts.value = postList
                    }
                }
                // 订阅故事列表流
                launch {
                    repository.observeStories().collect { storyList ->
                        Timber.d("[FeedViewModel] 收到故事列表更新，共 %d 条", storyList.size)
                        _stories.value = storyList
                    }
                }
            } catch (t: Throwable) {
                Timber.e(t, "[FeedViewModel] 加载数据失败")
                _error.value = t.message ?: "未知错误"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
