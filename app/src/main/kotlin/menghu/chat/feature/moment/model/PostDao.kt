package menghu.chat.feature.moment.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 动态（帖子）DAO
 */
@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    /** 按创建时间倒序查询所有动态 */
    @Query("SELECT * FROM post ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM post ORDER BY createdAt DESC")
    suspend fun getAllPostsList(): List<PostEntity>

    @Query("SELECT * FROM post WHERE id = :id")
    suspend fun getPostById(id: Long): PostEntity?

    @Query("DELETE FROM post WHERE id = :id")
    suspend fun deletePost(id: Long)

    @Query("DELETE FROM post")
    suspend fun clearAll()
}
