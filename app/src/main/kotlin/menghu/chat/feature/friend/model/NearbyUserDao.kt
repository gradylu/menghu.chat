package menghu.chat.feature.friend.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 附近的用户 DAO
 */
@Dao
interface NearbyUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNearbyUser(user: NearbyUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNearbyUsers(users: List<NearbyUserEntity>)

    @Query("SELECT * FROM nearby_user ORDER BY distanceKm ASC")
    fun getAllNearbyUsers(): Flow<List<NearbyUserEntity>>

    @Query("SELECT * FROM nearby_user ORDER BY distanceKm ASC")
    suspend fun getAllNearbyUsersList(): List<NearbyUserEntity>

    @Query("DELETE FROM nearby_user")
    suspend fun clearAll()
}
