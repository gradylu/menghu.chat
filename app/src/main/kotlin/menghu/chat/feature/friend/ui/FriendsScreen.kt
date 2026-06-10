package menghu.chat.feature.friend.ui

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import menghu.chat.core.ui.component.CommonTopBar
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.feature.friend.model.FriendEntity
import menghu.chat.feature.friend.viewmodel.FriendsViewModel
import menghu.chat.navigation.Destinations
import timber.log.Timber

/**
 * 好友列表页 FriendsScreen
 * - Scaffold：顶部标题栏（菜单按钮 + "Friends" 标题 + 通知按钮）
 * - 搜索框：OutlinedTextField 圆角输入
 * - LazyVerticalGrid(2) 两列网格卡片：大图头像（3:4）+ 昵称 + 国家
 * - 右侧纵向 A-Z 字母索引条（点击 Toast 反馈）
 * - 点击卡片导航到 FollowerProfile
 */
@Composable
fun FriendsScreen(
    navController: NavController,
    viewModel: FriendsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.ensureDataReady()
    }
    val keyword by viewModel.keyword.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        containerColor = AppColors.Background,
        // ✅ 使用共享 CommonTopBar：标题居中、橙色标题、白色背景、一致的菜单/通知图标
        topBar = {
            CommonTopBar(
                title = "Friends",
                onMenuClick = {},
                onNotificationClick = { navController.navigate(Destinations.Notification.route) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ===== 搜索框 =====
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = keyword,
                    onValueChange = { viewModel.onKeywordChanged(it) },
                    placeholder = {
                        Text(text = "Search friends...", color = AppColors.TextHint)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.OrangePrimary,
                        unfocusedBorderColor = AppColors.Border,
                        cursorColor = AppColors.OrangePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ===== 主体：左侧 2 列网格 + 右侧字母索引 =====
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (isLoading && friends.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AppColors.OrangePrimary)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                start = 12.dp,
                                end = 4.dp,
                                top = 4.dp,
                                bottom = 12.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(friends, key = { it.userId }) { friend ->
                                FriendGridCard(
                                    friend = friend,
                                    onClick = {
                                        Timber.d("[FriendsScreen] 点击好友 %s，跳转到资料页", friend.name)
                                        navController.navigate(
                                            Destinations.FollowerProfile.withUserId(friend.userId)
                                        )
                                    }
                                )
                            }
                        }

                        // ===== 右侧 A-Z 字母索引条 =====
                        Column(
                            modifier = Modifier
                                .width(28.dp)
                                .fillMaxSize()
                                .padding(end = 2.dp),
                            verticalArrangement = Arrangement.spacedBy(
                                2.dp,
                                Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ('A'..'Z').forEach { letter ->
                                Text(
                                    text = letter.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.OrangePrimary,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Jump to $letter",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                        .wrapContentHeight(Alignment.CenterVertically)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                        .padding(2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 好友网格卡片：大图头像（3:4）+ 姓名 + 国家 */
@Composable
private fun FriendGridCard(
    friend: FriendEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            // 大图头像 3:4
            AsyncImage(
                model = friend.avatar,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = friend.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = friend.country,
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}
