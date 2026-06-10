package menghu.chat.feature.moment.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import menghu.chat.core.ui.component.CircleAvatar
import menghu.chat.core.ui.component.CommonTopBar
import menghu.chat.core.ui.component.PostCard
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.feature.moment.model.StoryEntity
import menghu.chat.feature.moment.viewmodel.FeedViewModel
import menghu.chat.navigation.Destinations

/**
 * Feed 动态流页面
 * 包含：顶部 Feeds 标题、横向故事条、动态卡片列表
 *
 * @param navController 路由控制器（用于跳转到故事、评论等页面）
 * @param viewModel Feed 数据 ViewModel（Hilt 注入）
 */
@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: FeedViewModel = hiltViewModel()
) {
    // 通过生命周期感知的 collectAsStateWithLifecycle 收集 StateFlow
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Scaffold(
        // ✅ 使用共享 CommonTopBar：标题居中、橙色标题、统一的菜单/通知图标
        topBar = {
            CommonTopBar(
                title = "Feeds",
                onMenuClick = {},
                onNotificationClick = { navController.navigate(Destinations.Notification.route) }
            )
        },
        containerColor = AppColors.Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading && posts.isEmpty() -> {
                    // 首次加载时的居中 Loading
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.OrangePrimary)
                    }
                }
                !error.isNullOrEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "加载失败：$error", color = AppColors.Error)
                    }
                }
                else -> {
                    // 正常动态流
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 横向故事行
                        item {
                            StoryRow(
                                stories = stories,
                                onAddStoryClick = {
                                    // 点击加号跳转到发布页
                                    navController.navigate(Destinations.NewPost.route)
                                },
                                onStoryClick = {
                                    // 点击故事跳转 Story 页
                                    navController.navigate(Destinations.Story.route)
                                }
                            )
                        }

                        // 动态卡片列表
                        items(posts, key = { it.id }) { post ->
                            PostCard(
                                post = post,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                onCommentsClick = {
                                    navController.navigate(Destinations.Comments.withPostId(post.id))
                                }
                            )
                        }

                        // 底部留白
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * 横向故事行（LazyRow）
 * 第一项为"发布新故事"，其他为用户故事圆形头像
 */
@Composable
private fun StoryRow(
    stories: List<StoryEntity>,
    onAddStoryClick: () -> Unit,
    onStoryClick: (StoryEntity) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "+ 发布故事" 按钮
        item {
            AddStoryItem(onClick = onAddStoryClick)
        }

        // 用户故事列表（紫色外圈）
        items(stories, key = { it.id }) { story ->
            StoryAvatarItem(
                story = story,
                onClick = { onStoryClick(story) }
            )
        }
    }
}

/**
 * 发布故事按钮：圆形头像内显示蓝色 "+"
 */
@Composable
private fun AddStoryItem(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(
                    BorderStroke(1.dp, AppColors.Border),
                    CircleShape
                )
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OrangePrimary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Your story",
            fontSize = 12.sp,
            color = AppColors.TextSecondary
        )
    }
}

/**
 * 用户故事头像项：外圈紫色边框 + 头像 + 昵称
 */
@Composable
private fun StoryAvatarItem(
    story: StoryEntity,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        CircleAvatar(
            avatarUrl = story.authorAvatar,
            name = story.authorName,
            size = 64.dp,
            showPurpleRing = true
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = story.authorName,
            fontSize = 12.sp,
            color = AppColors.TextSecondary
        )
    }
}
