package menghu.chat.feature.moment.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 评论 DAO
 */
@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    /** 按时间倒序查询某条动态下的所有评论 */
    @Query("SELECT * FROM comment WHERE postId = :postId ORDER BY createdAt DESC")
    fun getCommentsByPost(postId: Long): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comment WHERE postId = :postId ORDER BY createdAt DESC")
    suspend fun getCommentsByPostList(postId: Long): List<CommentEntity>

    @Query("DELETE FROM comment WHERE postId = :postId")
    suspend fun deleteCommentsByPost(postId: Long)

    @Query("DELETE FROM comment")
    suspend fun clearAll()
}
