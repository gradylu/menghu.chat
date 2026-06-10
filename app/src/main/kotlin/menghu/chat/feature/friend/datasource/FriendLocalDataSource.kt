package menghu.chat.feature.friend.datasource

import kotlinx.coroutines.flow.Flow
import menghu.chat.feature.friend.model.FriendDao
import menghu.chat.feature.friend.model.FriendEntity
import menghu.chat.feature.friend.model.NearbyUserDao
import menghu.chat.feature.friend.model.NearbyUserEntity
import timber.log.Timber
import javax.inject.Inject

/**
 * 好友 / 附近的人本地数据源
 * - 聚合 FriendDao、NearbyUserDao 的读写能力
 * - Repository 层通过本数据源访问 Room 数据库
 */
class FriendLocalDataSource @Inject constructor(
    private val friendDao: FriendDao,
    private val nearbyUserDao: NearbyUserDao
) {

    // ======================= 好友相关 =======================

    suspend fun saveFriend(friend: FriendEntity) {
        Timber.d("[FriendLocalDataSource] 保存好友: id=%s", friend.id)
        friendDao.insertFriend(friend)
    }

    suspend fun saveFriends(list: List<FriendEntity>) = friendDao.insertFriends(list)

    fun observeFriends(): Flow<List<FriendEntity>> = friendDao.getAllFriends()

    fun searchFriends(keyword: String): Flow<List<FriendEntity>> = friendDao.searchFriends(keyword)

    suspend fun getFriends(): List<FriendEntity> = friendDao.getAllFriendsList()

    suspend fun clearFriends() = friendDao.clearAll()

    // ======================= 附近的人相关 =======================

    suspend fun saveNearbyUser(user: NearbyUserEntity) = nearbyUserDao.insertNearbyUser(user)

    suspend fun saveNearbyUsers(list: List<NearbyUserEntity>) = nearbyUserDao.insertNearbyUsers(list)

    fun observeNearbyUsers(): Flow<List<NearbyUserEntity>> = nearbyUserDao.getAllNearbyUsers()

    suspend fun getNearbyUsers(): List<NearbyUserEntity> = nearbyUserDao.getAllNearbyUsersList()

    suspend fun clearNearbyUsers() = nearbyUserDao.clearAll()
}
