package menghu.chat.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImage
import menghu.chat.core.ui.component.CircleAvatar
import menghu.chat.core.ui.component.CommonTopBar
import menghu.chat.core.ui.component.PostCard
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.feature.moment.model.PostEntity
import menghu.chat.feature.profile.viewmodel.ProfileViewModel
import timber.log.Timber

/**
 * 个人主页 Screen
 * - 顶部：CommonTopBar（Profile 标题 + 铃铛）
 * - 头部：大圆头像 + 昵称 + 职业 + 签名
 * - 统计行：Post / Followers / Friends
 * - Tab 行：Posts / Collection，橙色下划线联动
 * - 内容区：
 *   - Posts Tab：LazyColumn 动态卡片列表
 *   - Collection Tab：LazyVerticalGrid 2 列收藏网格
 *
 * @param viewModel ViewModel（Hilt 注入）
 * @param onNotificationClick 顶部铃铛回调
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNotificationClick: () -> Unit = { Timber.d("[ProfileScreen] 点击通知") }
) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val collections by viewModel.collections.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Profile",
                onMenuClick = { Timber.d("[ProfileScreen] 点击菜单") },
                onNotificationClick = onNotificationClick,
                hasUnreadNotification = false
            )
        },
        containerColor = AppColors.Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading || user == null) {
                // 加载占位
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.OrangePrimary)
                }
            } else {
                // 可滚动主体：头部 + 统计 + Tab + 内容
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 区域 1：用户资料头部
                    item {
                        ProfileHeader(
                            avatar = user!!.avatar,
                            name = user!!.name,
                            occupation = user!!.occupation,
                            bio = user!!.bio
                        )
                    }

                    // 区域 2：统计行
                    item {
                        ProfileStatsRow(
                            postCount = user!!.postCount,
                            followerCount = user!!.followerCount,
                            friendCount = user!!.friendCount
                        )
                    }

                    // 区域 3：Tab 行 + 内容（根据选中 Tab 渲染）
                    item {
                        var selectedTab by remember { mutableIntStateOf(0) }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            ProfileTabRow(selectedIndex = selectedTab) { index ->
                                selectedTab = index
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            when (selectedTab) {
                                0 -> PostListContent(posts = posts)
                                1 -> CollectionGridContent(collections = collections)
                            }
                        }
                    }

                    // 底部留白
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

/**
 * 头部资料：左侧大头像 + 右侧三行信息（昵称 / 职业 / 签名）
 */
@Composable
private fun ProfileHeader(
    avatar: String,
    name: String,
    occupation: String,
    bio: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 大圆头像
        CircleAvatar(
            avatarUrl = avatar,
            name = name,
            size = 92.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
        )
        Spacer(modifier = Modifier.size(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = occupation,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.OrangePrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = bio,
                fontSize = 13.sp,
                color = AppColors.TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * 统计行：三个等宽 Column，数字在上，标签在下
 */
@Composable
private fun ProfileStatsRow(
    postCount: Int,
    followerCount: Int,
    friendCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        ProfileStatItem(
            label = "Post",
            value = formatBigNumber(postCount),
            modifier = Modifier.weight(1f)
        )
        ProfileStatItem(
            label = "Followers",
            value = formatBigNumber(followerCount),
            modifier = Modifier.weight(1f)
        )
        ProfileStatItem(
            label = "Friends",
            value = formatBigNumber(friendCount),
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * 单个统计项：数字 + 标签
 */
@Composable
private fun ProfileStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = AppColors.TextSecondary
        )
    }
}

/**
 * Tab 行：Posts / Collection，选中状态使用橙色下划线指示器
 */
@Composable
private fun ProfileTabRow(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val tabs = listOf("Posts", "Collection")
    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        containerColor = Color.Transparent,
        contentColor = AppColors.OrangePrimary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedIndex])
                    .padding(horizontal = 40.dp),
                height = 3.dp,
                color = AppColors.OrangePrimary
            )
        },
        divider = { /* 不显示默认分割线 */ }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                text = {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (selectedIndex == index) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (selectedIndex == index) AppColors.OrangePrimary else AppColors.TextSecondary
                    )
                }
            )
        }
    }
}

/**
 * Posts Tab 内容：动态卡片列表
 */
@Composable
private fun PostListContent(posts: List<PostEntity>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (posts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无动态",
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary
                )
            }
        } else {
            posts.forEach { post ->
                PostCard(
                    post = post,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onCommentsClick = { Timber.d("[ProfileScreen] 点击动态评论: postId=%s", post.id) }
                )
            }
        }
    }
}

/**
 * Collection Tab 内容：2 列图片网格
 */
@Composable
private fun CollectionGridContent(collections: List<String>) {
    if (collections.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无收藏",
                fontSize = 14.sp,
                color = AppColors.TextSecondary
            )
        }
        return
    }

    // 使用 LazyVerticalGrid 2 列网格
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(collections.size) { index ->
            CollectionGridItem(url = collections[index])
        }
    }
}

/**
 * 收藏卡片：圆角图片，可点击
 */
@Composable
private fun CollectionGridItem(url: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { Timber.d("[ProfileScreen] 点击收藏: %s", url) }
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * 数字格式化：
 * - 小于 1000：原样显示
 * - 小于 10000：保留一位小数，如 "2.1k"
 * - 更大：按 M 显示（"2.1m"）
 */
private fun formatBigNumber(value: Int): String {
    return when {
        value < 1000 -> value.toString()
        value < 1_000_000 -> {
            val d = value / 1000.0
            if ((d * 10).toInt() % 10 == 0) "${d.toInt()}k"
            else "%.1fk".format(d)
        }
        else -> {
            val d = value / 1_000_000.0
            if ((d * 10).toInt() % 10 == 0) "${d.toInt()}m"
            else "%.1fm".format(d)
        }
    }
}
