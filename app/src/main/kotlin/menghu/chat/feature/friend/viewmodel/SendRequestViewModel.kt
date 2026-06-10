package menghu.chat.feature.friend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import menghu.chat.feature.friend.model.NearbyUserEntity
import menghu.chat.feature.friend.repository.FriendRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 发送好友请求页 ViewModel
 * - 启动时确保附近用户数据初始化
 * - 通过 userId 查询目标用户，供 SendRequestScreen 展示
 */
@HiltViewModel
class SendRequestViewModel @Inject constructor(
    private val repository: FriendRepository
) : ViewModel() {

    /** 已加载的附近用户列表（供按 userId 查询） */
    private val _users = MutableStateFlow<List<NearbyUserEntity>>(emptyList())
    val users: StateFlow<List<NearbyUserEntity>> = _users.asStateFlow()

    /** 加载附近用户列表，先确保 Mock 数据已注入 */
    fun loadUsers() {
        viewModelScope.launch {
            Timber.d("[SendRequestVM] 加载附近用户列表")
            repository.insertMockIfEmpty()
            _users.value = repository.getNearbyUsers()
        }
    }
}
