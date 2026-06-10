package menghu.chat.feature.chat.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 聊天会话数据库实体
 */
@Entity(tableName = "conversation")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    /** 会话对方用户 ID */
    var peerId: Long = 0,

    /** 对方昵称 */
    var peerName: String = "",

    /** 对方头像 */
    var peerAvatar: String = "",

    /** 最后一条消息 */
    var lastMessage: String = "",

    /** 最后一条消息时间 */
    var lastMessageAt: Long = System.currentTimeMillis(),

    /** 未读数 */
    var unreadCount: Int = 0,

    /** 是否置顶 */
    var isPinned: Boolean = false,

    /** 对方是否在线 */
    var peerOnline: Boolean = false
)
