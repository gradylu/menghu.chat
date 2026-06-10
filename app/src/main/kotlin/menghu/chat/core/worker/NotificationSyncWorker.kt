package menghu.chat.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import menghu.chat.feature.notification.model.NotificationDao
import menghu.chat.feature.notification.model.NotificationEntity
import timber.log.Timber

/**
 * 通知同步 Worker
 * 使用 Hilt 注入的 DAO，周期性（或一次性）模拟生成一条通知并写入本地 Room 数据库
 *
 * @param appContext 应用上下文
 * @param params Worker 参数
 * @param notificationDao 通知 DAO（由 Hilt 注入）
 */
@HiltWorker
class NotificationSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val notificationDao: NotificationDao
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Timber.d("[NotificationSyncWorker] 开始同步通知...")

        return try {
            // 模拟生成一条系统通知并插入数据库
            val mockNotification = NotificationEntity(
                id = 0, // Room 自动分配自增主键
                type = "system",
                title = "系统消息",
                content = "你有一条新的系统消息，请查看",
                relatedUserId = 0,
                relatedUserName = "系统",
                relatedUserAvatar = "",
                isRead = false,
                createdAt = System.currentTimeMillis()
            )
            notificationDao.insertNotification(mockNotification)

            Timber.d("[NotificationSyncWorker] 通知同步完成，已插入 1 条通知")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "[NotificationSyncWorker] 通知同步失败")
            Result.retry()
        }
    }
}
