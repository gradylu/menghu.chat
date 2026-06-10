package menghu.chat.feature.moment.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import menghu.chat.core.ui.component.CircleAvatar
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.feature.moment.viewmodel.FeedViewModel
import menghu.chat.core.utils.toRelativeTime

/**
 * 故事全屏展示页
 * 全屏大图 + 顶部作者信息 + 底部评论输入条
 *
 * @param navController 路由控制器
 * @param viewModel 共享 FeedViewModel 以获取故事列表
 */
@Composable
fun StoryScreen(
    navController: NavController,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    // 取第 1 条故事进行展示（若无则展示占位）
    val story = stories.firstOrNull()
    var commentText by remember { mutableStateOf("") }
    var toastText by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { /* 点击空白处返回 */ navController.popBackStack() }
    ) {
        // 全屏大图背景
        AsyncImage(
            model = story?.mediaUrl ?: "https://picsum.photos/seed/story/900/1400",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 顶部信息栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleAvatar(
                avatarUrl = story?.authorAvatar,
                name = story?.authorName ?: "User",
                size = 40.dp,
                showPurpleRing = true
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = story?.authorName ?: "User",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = (story?.createdAt ?: System.currentTimeMillis()).toRelativeTime(),
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "✕",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { navController.popBackStack() }
                    .padding(4.dp)
            )
        }

        // 底部评论输入条
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_gallery),
                    contentDescription = "附件",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = {
                        Text(
                            text = "be the first to comment",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Black.copy(alpha = 0.35f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.35f)
                    ),
                    singleLine = true
                )

                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_myplaces),
                    contentDescription = "表情",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )

                // 发送按钮：圆形紫色背景
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AppColors.PurpleAccent)
                        .clickable {
                            toastText = if (commentText.isBlank()) {
                                "请输入评论内容"
                            } else {
                                "评论已发送"
                            }
                            commentText = ""
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_send),
                        contentDescription = "发送",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 轻量 toast 反馈（Composable 内简易实现）
            if (toastText != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = toastText.orEmpty(),
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
