package menghu.chat.feature.friend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import menghu.chat.core.ui.component.PostCard
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.feature.friend.viewmodel.FollowerProfileViewModel
import menghu.chat.feature.friend.viewmodel.ProfileTab
import timber.log.Timber

/**
 * 关注者/他人资料页（Follower Profile）
 * - 顶部：返回按钮 + 空标题 + 通知铃铛
 * - 头像（圆形大头像）+ 昵称 + 职业标签 + 个性签名
 * - 统计行：Posts / Followers / Friends 计数
 * - 按钮行：Follow/Following 主按钮 + Chat 次按钮
 * - Tab 行：Posts / Collection 橙色下划线
 * - 动态卡片列表（复用 PostCard）
 *
 * @param navController 路由控制器
 * @param userId 目标用户 ID
 * @param userName 可选的用户姓名回退
 * @param viewModel Hilt 注入的 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowerProfileScreen(
    navController: NavController,
    userId: Long,
    userName: String? = null,
    viewModel: FollowerProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(userId) {
        Timber.d("[FollowerProfileScreen] 加载用户 %s 的资料", userId)
        viewModel.loadProfile(userId, userName)
    }

    val name by viewModel.name.collectAsStateWithLifecycle()
    val avatar by viewModel.avatar.collectAsStateWithLifecycle()
    val profession by viewModel.profession.collectAsStateWithLifecycle()
    val bio by viewModel.bio.collectAsStateWithLifecycle()
    val postCount by viewModel.postCount.collectAsStateWithLifecycle()
    val followerCount by viewModel.followerCount.collectAsStateWithLifecycle()
    val friendCount by viewModel.friendCount.collectAsStateWithLifecycle()
    val isFollowing by viewModel.isFollowing.collectAsStateWithLifecycle()
    val tab by viewModel.tab.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = { /* 标题留空，让昵称在下方展示 */ },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* 预留通知入口 */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "通知",
                            tint = AppColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Background,
                    titleContentColor = AppColors.TextPrimary
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColors.OrangePrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ===== 头像 + 姓名 + 职业 =====
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 圆形大头像，带紫色外环
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(AppColors.PurpleAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = avatar,
                                contentDescription = "头像",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = profession,
                            fontSize = 14.sp,
                            color = AppColors.OrangePrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = bio,
                            fontSize = 13.sp,
                            color = AppColors.TextSecondary,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }

                // ===== 统计行 =====
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatColumn(label = "Posts", value = formatBigNumber(postCount))
                        StatColumn(label = "Followers", value = formatBigNumber(followerCount))
                        StatColumn(label = "Friends", value = formatBigNumber(friendCount))
                    }
                }

                // ===== 按钮行：Follow + Chat =====
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.toggleFollow() },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.OrangePrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (isFollowing) "Following" else "Follow",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                Timber.d("[FollowerProfile] 点击 Chat，跳转到 Chat 页（mock conversationId=-1）")
                                // 跳转到 Chat 路由，传一个固定 conversationId 表示临时会话
                                navController.navigate("chat/-1")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = AppColors.TextPrimary
                            )
                        ) {
                            Text(
                                text = "Chat",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // ===== Tab 行：Posts / Collection =====
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TabItem(
                            label = "Posts",
                            selected = tab == ProfileTab.Posts,
                            onClick = { viewModel.selectTab(ProfileTab.Posts) },
                            modifier = Modifier.weight(1f)
                        )
                        TabItem(
                            label = "Collection",
                            selected = tab == ProfileTab.Collection,
                            onClick = { viewModel.selectTab(ProfileTab.Collection) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ===== 动态卡片列表（Posts 时展示，Collection 时展示占位）=====
                item { Spacer(modifier = Modifier.height(8.dp)) }
                when (tab) {
                    ProfileTab.Posts -> {
                        if (posts.isEmpty()) {
                            item {
                                EmptyHint(text = "暂无动态")
                            }
                        } else {
                            items(posts, key = { it.id + it.createdAt }) { post ->
                                PostCard(post = post)
                            }
                        }
                    }
                    ProfileTab.Collection -> {
                        item {
                            EmptyHint(text = "Collection 功能建设中")
                        }
                    }
                }

                // 底部留白
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

/** 统计列：数字 + 标签 */
@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = AppColors.TextSecondary
        )
    }
}

/** Tab 项（选中态底部橙色下划线） */
@Composable
private fun TabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) AppColors.OrangePrimary else AppColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    if (selected) AppColors.OrangePrimary else Color.Transparent
                )
        )
    }
}

/** 空状态提示 */
@Composable
private fun EmptyHint(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = AppColors.TextSecondary,
            fontSize = 14.sp
        )
    }
}

/** 将整数格式化为易读大数（千/百万） */
private fun formatBigNumber(value: Int): String {
    return when {
        value < 1000 -> value.toString()
        value < 1_000_000 -> {
            val d = value / 1000.0
            if ((d * 10).toInt() % 10 == 0) "${d.toInt()}k" else "%.1fk".format(d)
        }
        else -> {
            val d = value / 1_000_000.0
            if ((d * 10).toInt() % 10 == 0) "${d.toInt()}m" else "%.1fm".format(d)
        }
    }
}
