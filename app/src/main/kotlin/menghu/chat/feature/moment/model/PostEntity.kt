package menghu.chat.feature.moment.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 动态（帖子）数据库实体
 */
@Entity(tableName = "post")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    /** 发布者用户 ID */
    var authorId: Long = 0,

    /** 发布者昵称（冗余字段，避免连表查询） */
    var authorName: String = "",

    /** 发布者头像 */
    var authorAvatar: String = "",

    /** 文本内容 */
    var content: String = "",

    /** 图片列表（以英文逗号分隔存储） */
    var images: String = "",

    /** 点赞数 */
    var likeCount: Int = 0,

    /** 评论数 */
    var commentCount: Int = 0,

    /** 发布时间 */
    var createdAt: Long = System.currentTimeMillis()
)
