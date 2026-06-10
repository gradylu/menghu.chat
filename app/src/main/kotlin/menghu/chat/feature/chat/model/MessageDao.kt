package menghu.chat.feature.chat.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 聊天消息 DAO
 */
@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    /** 按时间升序查询某会话下的所有消息（聊天时从旧到新） */
    @Query("SELECT * FROM message WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    fun getMessagesByConversation(conversationId: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM message WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    suspend fun getMessagesByConversationList(conversationId: Long): List<MessageEntity>

    @Query("UPDATE message SET isRead = 1 WHERE conversationId = :conversationId")
    suspend fun markAllRead(conversationId: Long)

    @Query("DELETE FROM message WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversation(conversationId: Long)

    @Query("DELETE FROM message")
    suspend fun clearAll()
}
