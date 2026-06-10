package menghu.chat.feature.friend.repository

import kotlinx.coroutines.flow.Flow
import menghu.chat.feature.friend.datasource.FriendLocalDataSource
import menghu.chat.feature.friend.model.FriendEntity
import menghu.chat.feature.friend.model.NearbyUserEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 好友仓库层
 * - 聚合 Room 本地数据源，对外提供好友/附近用户的流式与一次性读写能力
 * - 首次启动时自动写入一组 Mock 数据，保证 UI 有内容可展示
 */
@Singleton
class FriendRepository @Inject constructor(
    private val localDataSource: FriendLocalDataSource
) {

    // ======================= 好友 =======================

    fun observeFriends(): Flow<List<FriendEntity>> = localDataSource.observeFriends()

    fun searchFriends(keyword: String): Flow<List<FriendEntity>> {
        val kw = keyword.trim()
        return if (kw.isEmpty()) {
            localDataSource.observeFriends()
        } else {
            localDataSource.searchFriends(kw)
        }
    }

    suspend fun getFriends(): List<FriendEntity> = localDataSource.getFriends()

    suspend fun addFriend(name: String, avatar: String, country: String = "") {
        Timber.i("[FriendRepository] 添加好友: %s (%s)", name, country)
        localDataSource.saveFriend(
            FriendEntity(
                id = 0,
                userId = (System.currentTimeMillis() % 10000).toLong(),
                name = name,
                avatar = avatar,
                country = country,
                status = "accepted",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    // ======================= 附近用户 =======================

    fun observeNearbyUsers(): Flow<List<NearbyUserEntity>> = localDataSource.observeNearbyUsers()

    suspend fun getNearbyUsers(): List<NearbyUserEntity> = localDataSource.getNearbyUsers()

    // ======================= Mock 数据 =======================

    /**
     * 若数据库为空，插入一组 Mock 好友与附近用户
     * - 好友：不同姓名 + 不同国家 + picsum 头像
     * - 附近用户：不同姓名 + 不同距离（2~20km）+ 头像
     */
    suspend fun insertMockIfEmpty() {
        val friends = localDataSource.getFriends()
        val nearby = localDataSource.getNearbyUsers()

        if (friends.isEmpty()) {
            Timber.i("[FriendRepository] 好友表为空，插入 Mock 好友数据")
            localDataSource.saveFriends(buildMockFriends())
        }
        if (nearby.isEmpty()) {
            Timber.i("[FriendRepository] 附近用户表为空，插入 Mock 附近用户数据")
            localDataSource.saveNearbyUsers(buildMockNearby())
        }
    }

    /** 构造 12 条 Mock 好友（不同国家 + 姓名 + picsum 头像大图 3:4） */
    private fun buildMockFriends(): List<FriendEntity> {
        val names = listOf(
            "Andrew Ruth" to "United States",
            "Sophia Martin" to "Spain",
            "Liam Chen" to "England",
            "Olivia Rossi" to "Italy",
            "Noah Dubois" to "France",
            "Emma Schmidt" to "Germany",
            "Aiko Tanaka" to "Japan",
            "Lucas Silva" to "Brazil",
            "Mia Johansson" to "Sweden",
            "Ethan O'Brien" to "Ireland",
            "Zoe Papadopoulos" to "Greece",
            "Hugo Müller" to "Austria"
        )
        return names.mapIndexed { index, pair ->
            FriendEntity(
                id = 0,
                userId = (1000L + index),
                name = pair.first,
                country = pair.second,
                avatar = "https://picsum.photos/seed/friend${index}/600/800",
                status = "accepted",
                createdAt = System.currentTimeMillis() - index * 3600_000L
            )
        }
    }

    /** 构造 12 条 Mock 附近用户（不同姓名 + 不同距离 + picsum 大图头像） */
    private fun buildMockNearby(): List<NearbyUserEntity> {
        val names = listOf(
            "Andrew Ruth", "Sophia Martin", "Liam Chen", "Olivia Rossi",
            "Noah Dubois", "Emma Schmidt", "Aiko Tanaka", "Lucas Silva",
            "Mia Johansson", "Ethan O'Brien", "Zoe Papadopoulos", "Hugo Müller"
        )
        val distances = listOf(2.0, 3.5, 5.0, 7.0, 10.0, 12.0, 13.0, 15.0, 17.0, 18.5, 20.0, 22.0)
        return names.mapIndexed { index, name ->
            NearbyUserEntity(
                id = 0,
                userId = (2000L + index),
                name = name,
                avatar = "https://picsum.photos/seed/nearby${index}/600/800",
                distanceKm = distances[index % distances.size],
                signature = "Hi, let's chat!",
                createdAt = System.currentTimeMillis() - index * 600_000L
            )
        }
    }
}
