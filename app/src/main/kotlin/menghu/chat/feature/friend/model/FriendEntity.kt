package menghu.chat.feature.friend.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 好友关系数据库实体
 * - 用于 Friends 页两列卡片网格展示
 * - 额外字段：country（国家名，用于卡片副标题展示）
 * - avatar 使用 picsum 风格图片 URL，大图 3:4 展示
 */
@Entity(tableName = "friend")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    /** 好友用户 ID（冗余，避免和业务用户表冲突） */
    var userId: Long = 0,

    /** 好友昵称（卡片大标题） */
    var name: String = "",

    /** 头像大图 URL（picsum 或其他图片源） */
    var avatar: String = "",

    /** 所属国家 / 地区（卡片副标题，如 United states / Spain） */
    var country: String = "",

    /** 关系状态：accepted / pending / blocked */
    var status: String = "accepted",

    /** 添加时间 */
    var createdAt: Long = System.currentTimeMillis()
)
