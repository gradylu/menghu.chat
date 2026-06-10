package menghu.chat.feature.moment.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 故事（Story）数据库实体
 */
@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    /** 发布者用户 ID */
    var authorId: Long = 0,

    /** 发布者昵称 */
    var authorName: String = "",

    /** 发布者头像 */
    var authorAvatar: String = "",

    /** 故事图片/视频 URL */
    var mediaUrl: String = "",

    /** 故事文本描述 */
    var caption: String = "",

    /** 过期时间（毫秒时间戳） */
    var expiresAt: Long = 0,

    /** 创建时间 */
    var createdAt: Long = System.currentTimeMillis()
)
