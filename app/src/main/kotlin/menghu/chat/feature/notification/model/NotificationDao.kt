package menghu.chat.feature.notification.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 通知 DAO
 */
@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("SELECT * FROM notification ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notification ORDER BY createdAt DESC")
    suspend fun getAllNotificationsList(): List<NotificationEntity>

    @Query("SELECT * FROM notification WHERE isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotifications(): Flow<List<NotificationEntity>>

    @Query("UPDATE notification SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notification SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notification WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    @Query("DELETE FROM notification")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM notification")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM notification WHERE isRead = 0")
    suspend fun countUnread(): Int
}
