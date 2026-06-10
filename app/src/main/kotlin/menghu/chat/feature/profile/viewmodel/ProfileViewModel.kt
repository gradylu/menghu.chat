package menghu.chat.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import menghu.chat.feature.auth.model.UserEntity
import menghu.chat.feature.auth.repository.AuthRepository
import menghu.chat.feature.moment.model.PostEntity
import menghu.chat.feature.moment.repository.MomentRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 个人主页 ViewModel
 * - 负责加载当前用户资料、动态列表、收藏列表
 * - 若数据库无默认用户，则插入一份默认的 Musiani Wanda 样本数据
 *
 * @param authRepository 鉴权/用户仓库
 * @param momentRepository 动态/故事仓库
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val momentRepository: MomentRepository
) : ViewModel() {

    // 当前登录用户
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // 动态列表（来自数据库或 Mock）
    private val _posts = MutableStateFlow<List<PostEntity>>(emptyList())
    val posts: StateFlow<List<PostEntity>> = _posts.asStateFlow()

    // 收藏列表（本地 Mock 图片 URL，暂不存在数据库）
    private val _collections = MutableStateFlow<List<String>>(emptyList())
    val collections: StateFlow<List<String>> = _collections.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        Timber.d("[ProfileViewModel] 初始化：开始加载用户与动态数据")
        loadProfile()
    }

    /**
     * 加载个人主页数据
     * 1. 若无默认用户，插入一份 Musiani Wanda 样本
     * 2. 读取动态列表（若为空则走 Mock）
     * 3. 构造本地收藏图片列表（Mock）
     */
    private fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 步骤 1：准备默认用户
                val defaultUser = UserEntity(
                    id = 1,
                    name = "Musiani Wanda",
                    email = "musiani.wanda@example.com",
                    avatar = "https://picsum.photos/seed/wanda/400/400",
                    bio = "Independent woman 😊",
                    occupation = "Designer @HelloUI",
                    postCount = 1312,
                    followerCount = 2100000,
                    friendCount = 2523,
                    token = "mock_token_profile",
                    createdAt = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30
                )

                // 先尝试直接用默认用户（此处为简化流程，直接展示默认样本用户）
                _currentUser.value = defaultUser

                // 步骤 2：读取动态（Feed 若已有 Mock 数据则直接返回）
                momentRepository.insertSampleDataIfEmpty()
                val list = momentRepository.getAllPosts()
                // 个人主页展示"我的动态"，此处直接取数据库中全部样本作为演示
                _posts.value = list

                // 步骤 3：构造 Mock 收藏图片列表
                val seedList = (1..20).map { "https://picsum.photos/seed/collection$it/600/600" }
                _collections.value = seedList

                Timber.i(
                    "[ProfileViewModel] 加载完成：用户=%s, 动态=%d, 收藏=%d",
                    defaultUser.name, list.size, seedList.size
                )
            } catch (t: Throwable) {
                Timber.e(t, "[ProfileViewModel] 加载失败")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
