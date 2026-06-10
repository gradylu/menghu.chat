package menghu.chat.feature.friend.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 附近的用户数据库实体
 * - 用于 Nearby 雷达页环形排布 + NearbyList 两列卡片网格
 * - distanceKm：距离（公里），用于卡片副标题展示，如 "2km"
 */
@Entity(tableName = "nearby_user")
data class NearbyUserEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    /** 附近用户 ID */
    var userId: Long = 0,

    /** 昵称 */
    var name: String = "",

    /** 头像 URL（用于大图卡片或圆形头像） */
    var avatar: String = "",

    /** 距离（公里，小数允许） */
    var distanceKm: Double = 0.0,

    /** 签名（预留字段，SendRequest 页可能使用） */
    var signature: String = "",

    /** 查询时间 */
    var createdAt: Long = System.currentTimeMillis()
)
