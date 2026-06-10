package menghu.chat.feature.friend.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 好友 DAO
 * - 全量流式读取 getAllFriends()
 * - 模糊搜索：按姓名 / 国家过滤
 * - 一次性读取 getAllFriendsList() 用于判断是否需要插入 Mock 数据
 */
@Dao
interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendEntity>)

    @Query("SELECT * FROM friend ORDER BY createdAt DESC")
    fun getAllFriends(): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friend ORDER BY createdAt DESC")
    suspend fun getAllFriendsList(): List<FriendEntity>

    /**
     * 模糊搜索好友（姓名或国家包含关键字，忽略大小写）
     */
    @Query(
        """SELECT * FROM friend
           WHERE LOWER(name) LIKE '%' || LOWER(:keyword) || '%'
              OR LOWER(country) LIKE '%' || LOWER(:keyword) || '%'
           ORDER BY createdAt DESC"""
    )
    fun searchFriends(keyword: String): Flow<List<FriendEntity>>

    @Query("DELETE FROM friend WHERE id = :id")
    suspend fun deleteFriend(id: Long)

    @Query("DELETE FROM friend")
    suspend fun clearAll()
}
