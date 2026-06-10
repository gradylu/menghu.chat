package menghu.chat.feature.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import menghu.chat.feature.notification.model.NotificationEntity
import menghu.chat.feature.notification.repository.NotificationRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 通知中心 ViewModel
 * - 负责流式读取通知列表、检测未读、清除单条/全部通知
 * - 初始化时若本地无数据则插入 Mock 样本
 *
 * @param repository 通知仓库层
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    // 通知列表（以 Flow 形式监听变化）
    val notifications: StateFlow<List<NotificationEntity>> get() =
        _notifications

    private val _notifications: MutableStateFlow<List<NotificationEntity>> =
        MutableStateFlow(emptyList())

    // 是否存在未读（来自 observe notifications 派生）
    private val _hasUnread = MutableStateFlow(false)
    val hasUnread: StateFlow<Boolean> = _hasUnread.asStateFlow()

    init {
        Timber.d("[NotificationViewModel] 初始化：准备通知数据")
        observeNotifications()
        insertMockIfEmpty()
    }

    /**
     * 监听通知列表变化（Room Flow），同时派生 hasUnread
     */
    private fun observeNotifications() {
        viewModelScope.launch {
            repository.observeNotifications().collect { list ->
                Timber.d("[NotificationViewModel] 收到 %d 条通知", list.size)
                _notifications.value = list
                _hasUnread.value = list.any { !it.isRead }
            }
        }
    }

    /**
     * 若无通知，则插入默认样本
     */
    private fun insertMockIfEmpty() {
        viewModelScope.launch {
            repository.insertMockIfEmpty()
        }
    }

    /**
     * 清除单条通知
     */
    fun clearNotification(id: Long) {
        viewModelScope.launch {
            Timber.d("[NotificationViewModel] 清除通知: id=%d", id)
            repository.clearNotification(id)
        }
    }

    /**
     * 清空全部通知
     */
    fun clearAll() {
        viewModelScope.launch {
            Timber.d("[NotificationViewModel] 清空全部通知")
            repository.clearAll()
        }
    }
}
