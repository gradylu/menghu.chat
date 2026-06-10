package menghu.chat.feature.moment.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 故事（Story）DAO
 */
@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("SELECT * FROM story WHERE expiresAt > :now ORDER BY createdAt DESC")
    fun getActiveStories(now: Long): Flow<List<StoryEntity>>

    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    suspend fun getAllStoriesList(): List<StoryEntity>

    @Query("DELETE FROM story WHERE expiresAt <= :now")
    suspend fun deleteExpiredStories(now: Long)

    @Query("DELETE FROM story")
    suspend fun clearAll()
}
