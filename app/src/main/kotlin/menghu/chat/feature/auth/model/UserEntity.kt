package menghu.chat.feature.auth.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户信息数据库实体
 * 对应本地 Room 数据库中的 user 表
 */
@Entity(tableName = "user")
data class UserEntity(
    /** 用户主键，自增 */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    /** 用户昵称 */
    var name: String = "",

    /** 邮箱地址 */
    var email: String = "",

    /** 头像 URL（Mock 场景可为占位符） */
    var avatar: String = "",

    /** 个人简介 */
    var bio: String = "",

    /** 职业 */
    var occupation: String = "",

    /** 动态数量 */
    var postCount: Int = 0,

    /** 粉丝数量 */
    var followerCount: Int = 0,

    /** 好友数量 */
    var friendCount: Int = 0,

    /** 登录 token（Mock 场景用） */
    var token: String = "",

    /** 创建时间（毫秒时间戳） */
    var createdAt: Long = System.currentTimeMillis()
)
