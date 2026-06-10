package menghu.chat.feature.chat.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 聊天消息数据库实体
 */
@Entity(tableName = "message")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    /** 所属会话 ID */
    var conversationId: Long = 0,

    /** 发送者用户 ID（0 表示自己） */
    var senderId: Long = 0,

    /** 发送者名称（便于 UI 直接显示） */
    var senderName: String = "",

    /** 消息类型：text / image / voice */
    var type: String = "text",

    /** 消息内容 */
    var content: String = "",

    /** 媒体资源 URL（图片/语音） */
    var mediaUrl: String = "",

    /** 是否已读 */
    var isRead: Boolean = false,

    /** 发送状态：sending / sent / failed */
    var status: String = "sent",

    /** 创建时间 */
    var createdAt: Long = System.currentTimeMillis()
) {
    /** 是否是自己发送的消息（senderId 为 0 代表自己） */
    val isMine: Boolean get() = senderId == 0L
}
