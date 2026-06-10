package menghu.chat.feature.friend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import menghu.chat.feature.friend.model.NearbyUserEntity
import menghu.chat.feature.friend.repository.FriendRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 附近用户列表 ViewModel
 * - 确保数据库就绪并插入 Mock 数据（若为空）
 * - 暴露附近用户列表 StateFlow
 */
@HiltViewModel
class NearbyListViewModel @Inject constructor(
    private val repository: FriendRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val users = repository.observeNearbyUsers()

    /** 确保数据准备好（首次进入插入 Mock 数据） */
    fun ensureDataReady() {
        viewModelScope.launch {
            _isLoading.value = true
            Timber.d("[NearbyListVM] 确保附近用户数据准备中")
            repository.insertMockIfEmpty()
            _isLoading.value = false
        }
    }
}
