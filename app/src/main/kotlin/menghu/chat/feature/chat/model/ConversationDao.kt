package menghu.chat.feature.chat.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * 聊天会话 DAO
 */
@Dao
interface ConversationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationEntity>)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("SELECT * FROM conversation ORDER BY isPinned DESC, lastMessageAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversation ORDER BY isPinned DESC, lastMessageAt DESC")
    suspend fun getAllConversationsList(): List<ConversationEntity>

    @Query("SELECT * FROM conversation WHERE id = :id")
    suspend fun getConversationById(id: Long): ConversationEntity?

    @Query("DELETE FROM conversation WHERE id = :id")
    suspend fun deleteConversation(id: Long)

    @Query("DELETE FROM conversation")
    suspend fun clearAll()
}
