package menghu.chat.feature.chat.datasource

import kotlinx.coroutines.flow.Flow
import menghu.chat.feature.chat.model.ConversationDao
import menghu.chat.feature.chat.model.ConversationEntity
import menghu.chat.feature.chat.model.MessageDao
import menghu.chat.feature.chat.model.MessageEntity
import timber.log.Timber
import javax.inject.Inject

/**
 * 聊天本地数据源
 * 封装会话与消息的 Room 操作
 */
class ChatLocalDataSource @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {

    // ========== 会话相关 ==========

    suspend fun saveConversation(conversation: ConversationEntity) {
        Timber.d("[ChatLocalDataSource] 保存会话: id=%s", conversation.id)
        conversationDao.insertConversation(conversation)
    }

    /** 更新已存在的会话记录（用于刷新最后一条消息预览、时间戳等）
     */
    suspend fun updateConversation(conversation: ConversationEntity) {
        conversationDao.insertConversation(conversation)
    }

    suspend fun saveConversations(list: List<ConversationEntity>) {
        conversationDao.insertConversations(list)
    }

    fun observeConversations(): Flow<List<ConversationEntity>> = conversationDao.getAllConversations()

    suspend fun getConversations(): List<ConversationEntity> = conversationDao.getAllConversationsList()

    suspend fun getConversationById(id: Long): ConversationEntity? = conversationDao.getConversationById(id)

    suspend fun deleteConversation(id: Long) = conversationDao.deleteConversation(id)

    // ========== 消息相关 ==========

    suspend fun saveMessage(message: MessageEntity) {
        Timber.d("[ChatLocalDataSource] 保存消息: id=%s", message.id)
        messageDao.insertMessage(message)
    }

    suspend fun saveMessages(list: List<MessageEntity>) = messageDao.insertMessages(list)

    fun observeMessages(conversationId: Long): Flow<List<MessageEntity>> =
        messageDao.getMessagesByConversation(conversationId)

    suspend fun getMessages(conversationId: Long): List<MessageEntity> =
        messageDao.getMessagesByConversationList(conversationId)

    suspend fun markAllRead(conversationId: Long) = messageDao.markAllRead(conversationId)

    suspend fun deleteMessagesByConversation(conversationId: Long) =
        messageDao.deleteMessagesByConversation(conversationId)
}
