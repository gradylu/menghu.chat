package menghu.chat.feature.moment.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 评论数据库实体
 * 对应本地 Room 数据库中的 comment 表，承载动态下的一级与子评论交互信息
 */
@Entity(tableName = "comment")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    /** 所属动态 ID */
    var postId: Long = 0,

    /** 评论者用户 ID */
    var authorId: Long = 0,

    /** 评论者昵称（冗余字段，避免连表查询） */
    var authorName: String = "",

    /** 评论者头像 */
    var authorAvatar: String = "",

    /** 评论内容 */
    var content: String = "",

    /** 子评论数量（默认 0，用于列表底部图标展示） */
    var commentCount: Int = 0,

    /** 点赞数量（默认 0，用于列表底部图标展示） */
    var likeCount: Int = 0,

    /** 评论时间 */
    var createdAt: Long = System.currentTimeMillis()
)

/**
 * 顶层扩展：将图片列表字符串（逗号分隔）解析为 URL 列表
 * 供 Feed 卡片、故事详情等需要多图展示的场景使用
 */
fun String.toImageList(): List<String> {
    if (this.isEmpty()) return emptyList()
    return this.split(",").filter { it.isNotBlank() }.map { it.trim() }
}
