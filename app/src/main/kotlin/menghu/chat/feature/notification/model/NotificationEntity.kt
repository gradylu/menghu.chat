package menghu.chat.feature.notification.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 通知数据库实体
 */
@Entity(tableName = "notification")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    /** 通知类型：like / comment / follow / system / message */
    var type: String = "system",

    /** 标题 */
    var title: String = "",

    /** 正文内容 */
    var content: String = "",

    /** 关联用户 ID（点赞/评论/关注者） */
    var relatedUserId: Long = 0,

    /** 关联用户昵称 */
    var relatedUserName: String = "",

    /** 关联用户头像 */
    var relatedUserAvatar: String = "",

    /** 是否已读 */
    var isRead: Boolean = false,

    /** 发生时间 */
    var createdAt: Long = System.currentTimeMillis()
)
