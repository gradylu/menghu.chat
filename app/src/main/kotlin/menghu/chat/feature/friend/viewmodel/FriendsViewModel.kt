package menghu.chat.feature.friend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import menghu.chat.feature.friend.model.FriendEntity
import menghu.chat.feature.friend.repository.FriendRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 好友列表 ViewModel
 * - 启动时检查数据库并插入 Mock 数据
 * - 持有搜索关键字，搜索结果，加载状态
 */
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendRepository
) : ViewModel() {

    /** 搜索关键字（空字符串表示全量展示） */
    private val _keyword = MutableStateFlow("")
    val keyword: StateFlow<String> = _keyword.asStateFlow()

    /** 加载状态 */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** 好友列表：根据关键字变化自动触发 searchFriends
     * 这里使用简单的 StateFlow 转换，避免引入过多协程依赖。
     * 由 UI 层 collect 即可
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val friends: StateFlow<List<FriendEntity>> = _keyword
        .flatMapLatest { kw -> repository.searchFriends(kw) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** 首次进入：若数据库为空，则自动插入 Mock 好友数据 */
    fun ensureDataReady() {
        viewModelScope.launch {
            _isLoading.value = true
            Timber.d("[FriendsVM] 确保好友数据准备中...")
            repository.insertMockIfEmpty()
            _isLoading.value = false
        }
    }

    /** 更新搜索关键字 */
    fun onKeywordChanged(kw: String) {
        _keyword.value = kw
    }
}
