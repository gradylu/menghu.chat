package menghu.chat.feature.notification.repository

import kotlinx.coroutines.flow.Flow
import menghu.chat.feature.notification.datasource.NotificationLocalDataSource
import menghu.chat.feature.notification.model.NotificationEntity
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 通知仓库层
 * 聚合本地数据源，提供通知列表、清除、Mock 数据插入等能力
 * ViewModel 层通过本 Repository 获取或修改通知数据
 */
@Singleton
class NotificationRepository @Inject constructor(
    private val localDataSource: NotificationLocalDataSource
) {

    /** 流式观察通知列表（按时间倒序） */
    fun observeNotifications(): Flow<List<NotificationEntity>> =
        localDataSource.observeNotifications()

    /** 一次性获取通知列表 */
    suspend fun getNotifications(): List<NotificationEntity> =
        localDataSource.getNotifications()

    /** 将所有通知标记为已读 */
    suspend fun markAllAsRead() = localDataSource.markAllAsRead()

    /** 标记单条通知已读 */
    suspend fun markAsRead(id: Long) = localDataSource.markAsRead(id)

    /** 清除指定 ID 的通知 */
    suspend fun clearNotification(id: Long) {
        Timber.d("[NotificationRepository] 删除通知: id=%d", id)
        localDataSource.deleteNotification(id)
    }

    /** 清空全部通知 */
    suspend fun clearAll() {
        Timber.d("[NotificationRepository] 清空全部通知")
        localDataSource.clearAll()
    }

    /** 是否存在未读通知 */
    suspend fun hasUnread(): Boolean = localDataSource.countUnread() > 0

    /**
     * 若数据库为空，插入一组默认 Mock 通知
     * 覆盖 Today / Yesterday / This week 三个分组，每组 3 条
     */
    suspend fun insertMockIfEmpty() {
        val count = localDataSource.count()
        if (count > 0) {
            Timber.i("[NotificationRepository] 已有 %d 条通知，跳过 Mock 插入", count)
            return
        }

        val now = System.currentTimeMillis()
        val minute = TimeUnit.MINUTES.toMillis(1)
        val hour = TimeUnit.HOURS.toMillis(1)
        val day = TimeUnit.DAYS.toMillis(1)

        val mockList = listOf(
            // ============ Today ============
            NotificationEntity(
                id = 0,
                type = "like_story",
                title = "",
                content = "Like your story",
                relatedUserId = 201,
                relatedUserName = "Ava Thompson",
                relatedUserAvatar = "https://picsum.photos/seed/ava/160/160",
                isRead = false,
                createdAt = now - 1 * minute
            ),
            NotificationEntity(
                id = 0,
                type = "respond_story",
                title = "",
                content = "respond to your story",
                relatedUserId = 202,
                relatedUserName = "Liam Carter",
                relatedUserAvatar = "https://picsum.photos/seed/liam/160/160",
                isRead = false,
                createdAt = now - 20 * minute
            ),
            NotificationEntity(
                id = 0,
                type = "started_follow",
                title = "",
                content = "Started follow you",
                relatedUserId = 203,
                relatedUserName = "Sophia Nguyen",
                relatedUserAvatar = "https://picsum.photos/seed/sophia/160/160",
                isRead = true,
                createdAt = now - 2 * hour
            ),

            // ============ Yesterday ============
            NotificationEntity(
                id = 0,
                type = "like_story",
                title = "",
                content = "Like your story",
                relatedUserId = 204,
                relatedUserName = "Noah Patel",
                relatedUserAvatar = "https://picsum.photos/seed/noah/160/160",
                isRead = false,
                createdAt = now - 1 * day + 2 * hour
            ),
            NotificationEntity(
                id = 0,
                type = "respond_story",
                title = "",
                content = "respond to your story",
                relatedUserId = 205,
                relatedUserName = "Emma Wilson",
                relatedUserAvatar = "https://picsum.photos/seed/emma/160/160",
                isRead = true,
                createdAt = now - 1 * day - 3 * hour
            ),
            NotificationEntity(
                id = 0,
                type = "started_follow",
                title = "",
                content = "Started follow you",
                relatedUserId = 206,
                relatedUserName = "Mason Lee",
                relatedUserAvatar = "https://picsum.photos/seed/mason/160/160",
                isRead = true,
                createdAt = now - 1 * day - 8 * hour
            ),

            // ============ This week ============
            NotificationEntity(
                id = 0,
                type = "like_story",
                title = "",
                content = "Like your story",
                relatedUserId = 207,
                relatedUserName = "Olivia Brown",
                relatedUserAvatar = "https://picsum.photos/seed/olivia/160/160",
                isRead = true,
                createdAt = now - 3 * day
            ),
            NotificationEntity(
                id = 0,
                type = "respond_story",
                title = "",
                content = "respond to your story",
                relatedUserId = 208,
                relatedUserName = "Ethan Garcia",
                relatedUserAvatar = "https://picsum.photos/seed/ethan/160/160",
                isRead = true,
                createdAt = now - 5 * day
            ),
            NotificationEntity(
                id = 0,
                type = "started_follow",
                title = "",
                content = "Started follow you",
                relatedUserId = 209,
                relatedUserName = "Isabella Davis",
                relatedUserAvatar = "https://picsum.photos/seed/isabella/160/160",
                isRead = true,
                createdAt = now - 6 * day
            )
        )

        localDataSource.saveNotifications(mockList)
        Timber.i("[NotificationRepository] 插入 %d 条 Mock 通知", mockList.size)
    }
}
