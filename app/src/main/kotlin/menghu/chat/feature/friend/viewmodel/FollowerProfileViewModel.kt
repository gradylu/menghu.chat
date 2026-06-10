package menghu.chat.feature.friend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import menghu.chat.feature.friend.model.FriendEntity
import menghu.chat.feature.friend.model.NearbyUserEntity
import menghu.chat.feature.friend.repository.FriendRepository
import menghu.chat.feature.moment.model.PostEntity
import menghu.chat.feature.moment.repository.MomentRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 粉丝/他人资料页 ViewModel
 * - 加载指定 userId 对应用户的资料（从好友表、附近用户表中查找，找不到则回退构造）
 * - 加载该用户发布的动态（从 PostEntity 中筛选 authorName 匹配者，若为空则构造 Mock）
 * - 维护关注状态 isFollowing，本地切换
 */
@HiltViewModel
class FollowerProfileViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val momentRepository: MomentRepository
) : ViewModel() {

    // ===== UI State：资料对象 =====
    // 注：为避免新增实体，这里用 Pair 聚合（姓名、头像、职业、签名、计数）

    /** 姓名 */
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    /** 头像 URL */
    private val _avatar = MutableStateFlow("")
    val avatar: StateFlow<String> = _avatar.asStateFlow()

    /** 职业标签，如 "Photographer @ Cannon" */
    private val _profession = MutableStateFlow("Photographer @ Cannon")
    val profession: StateFlow<String> = _profession.asStateFlow()

    /** 个性签名 */
    private val _bio = MutableStateFlow("Welcome to my profile. Let's share our travel stories together!")
    val bio: StateFlow<String> = _bio.asStateFlow()

    /** 动态数 */
    private val _postCount = MutableStateFlow(24_000)
    val postCount: StateFlow<Int> = _postCount.asStateFlow()

    /** 粉丝数 */
    private val _followerCount = MutableStateFlow(4_200_000)
    val followerCount: StateFlow<Int> = _followerCount.asStateFlow()

    /** 好友数 */
    private val _friendCount = MutableStateFlow(3_000)
    val friendCount: StateFlow<Int> = _friendCount.asStateFlow()

    /** 是否已关注 */
    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing.asStateFlow()

    /** 当前选中 Tab：Posts / Collection */
    private val _tab = MutableStateFlow(ProfileTab.Posts)
    val tab: StateFlow<ProfileTab> = _tab.asStateFlow()

    /** 当前用户动态列表 */
    private val _posts = MutableStateFlow<List<PostEntity>>(emptyList())
    val posts: StateFlow<List<PostEntity>> = _posts.asStateFlow()

    /** 加载状态 */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * 根据用户 ID 加载用户资料
     * - 优先从好友表查找；
     * - 未找到则从附近用户表查找；
     * - 仍未找到时根据 userName 构造回退资料；
     */
    fun loadProfile(userId: Long, userName: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            Timber.d("[FollowerProfileVM] 加载用户资料 userId=%s", userId)
            // 首次进入确保数据库不为空
            friendRepository.insertMockIfEmpty()
            momentRepository.insertSampleDataIfEmpty()

            val friends = friendRepository.getFriends()
            val nearby = friendRepository.getNearbyUsers()

            val fromFriend: FriendEntity? = friends.firstOrNull { it.userId == userId }
            val fromNearby: NearbyUserEntity? = nearby.firstOrNull { it.userId == userId }

            val displayName = fromFriend?.name
                ?: fromNearby?.name
                ?: userName
                ?: "Anonymous User"
            val avatarUrl = fromFriend?.avatar
                ?: fromNearby?.avatar
                ?: "https://picsum.photos/seed/user${userId}/600/800"

            _name.value = displayName
            _avatar.value = avatarUrl
            _profession.value = listOf(
                "Photographer @ Cannon",
                "Travel Blogger",
                "UI/UX Designer",
                "Software Engineer",
                "Foodie"
            ).random()
            _bio.value = "Welcome to my profile. Let's share our stories together!"

            // 从数据库加载该用户动态；若无匹配，构造 6 条 mock
            val allPosts = momentRepository.getAllPosts()
            val userPosts = allPosts.filter { it.authorName == displayName }
            if (userPosts.isNotEmpty()) {
                _posts.value = userPosts
            } else {
                Timber.d("[FollowerProfileVM] 未找到 %s 的动态，构造 Mock 数据", displayName)
                _posts.value = buildMockPosts(displayName, avatarUrl)
            }
            _postCount.value = _posts.value.size.coerceAtLeast(1) * 12 + 15
            _isLoading.value = false
        }
    }

    /** 切换关注/未关注 */
    fun toggleFollow() {
        _isFollowing.value = !_isFollowing.value
        Timber.d("[FollowerProfileVM] 切换关注状态 -> %s", _isFollowing.value)
    }

    /** 切换 Tab */
    fun selectTab(t: ProfileTab) {
        _tab.value = t
    }

    /** 构造一组模拟动态 */
    private fun buildMockPosts(authorName: String, authorAvatar: String): List<PostEntity> {
        val contents = listOf(
            "Beautiful sunset at the beach 🌅",
            "Tried a new coffee shop today, the espresso was amazing!",
            "Just finished reading a great book. Highly recommend it.",
            "Weekend hiking trip with friends ⛰️",
            "New photoshoot from yesterday's event!",
            "Morning run, fresh air, and a great start to the day."
        )
        return contents.mapIndexed { index, content ->
            PostEntity(
                id = 0,
                authorId = 0L,
                authorName = authorName,
                authorAvatar = authorAvatar,
                content = content,
                images = "https://picsum.photos/seed/${authorName}${index}/800/600",
                likeCount = (100..5000).random(),
                commentCount = (10..500).random(),
                createdAt = System.currentTimeMillis() - index * 3600_000L * 4
            )
        }
    }
}

/** Tab 枚举：Posts / Collection */
enum class ProfileTab { Posts, Collection }
