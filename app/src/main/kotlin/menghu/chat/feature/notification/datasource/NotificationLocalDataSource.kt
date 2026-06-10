package menghu.chat.feature.notification.datasource

import kotlinx.coroutines.flow.Flow
import menghu.chat.feature.notification.model.NotificationDao
import menghu.chat.feature.notification.model.NotificationEntity
import timber.log.Timber
import javax.inject.Inject

/**
 * 通知本地数据源
 */
class NotificationLocalDataSource @Inject constructor(
    private val notificationDao: NotificationDao
) {

    suspend fun saveNotification(notification: NotificationEntity) {
        Timber.d("[NotificationLocalDataSource] 保存通知: id=%s", notification.id)
        notificationDao.insertNotification(notification)
    }

    suspend fun saveNotifications(list: List<NotificationEntity>) =
        notificationDao.insertNotifications(list)

    fun observeNotifications(): Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()

    suspend fun getNotifications(): List<NotificationEntity> = notificationDao.getAllNotificationsList()

    suspend fun markAllAsRead() = notificationDao.markAllAsRead()

    suspend fun markAsRead(id: Long) = notificationDao.markAsRead(id)

    /** 删除指定 ID 的通知 */
    suspend fun deleteNotification(id: Long) = notificationDao.deleteNotification(id)

    /** 清空全部通知 */
    suspend fun clearAll() = notificationDao.clearAll()

    /** 通知总数（用于判断是否为空） */
    suspend fun count(): Int = notificationDao.count()

    /** 未读通知数量 */
    suspend fun countUnread(): Int = notificationDao.countUnread()
}
