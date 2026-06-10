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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import menghu.chat.core.ui.component.CircleAvatar
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.core.utils.toRelativeTime
import menghu.chat.feature.moment.model.CommentEntity
import menghu.chat.feature.moment.viewmodel.CommentsViewModel

/**
 * 评论页面
 * 顶部：动态作者信息 + 关闭按钮
 * 中部：评论列表
 * 底部：固定输入行
 *
 * @param navController 路由控制器
 * @param postId 所属动态 ID
 * @param authorName 动态作者昵称（用于顶部展示）
 * @param authorAvatar 动态作者头像
 * @param viewModel 评论 ViewModel（Hilt 注入）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    navController: NavController,
    postId: Long = 1L,
    authorName: String = "Hossein",
    authorAvatar: String = "https://picsum.photos/seed/hossein/160/160",
    viewModel: CommentsViewModel = hiltViewModel()
) {
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var toast by remember { mutableStateOf<String?>(null) }

    // 进入页面时加载评论
    LaunchedEffect(postId) {
        viewModel.loadComments(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_media_previous),
                            contentDescription = "返回",
                            tint = AppColors.TextPrimary,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { navController.popBackStack() }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CircleAvatar(
                            avatarUrl = authorAvatar,
                            name = authorName,
                            size = 32.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Comments · ${formatCountK(comments.size)} from $authorName",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "2 minutes ago",
                                fontSize = 11.sp,
                                color = AppColors.TextSecondary
                            )
                        }
                    }
                },
                actions = {
                    Text(
                        text = "✕",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { navController.popBackStack() }
                            .padding(6.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Background,
                    titleContentColor = AppColors.TextPrimary
                )
            )
        },
        containerColor = AppColors.Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 评论列表
            if (comments.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "暂无评论",
                            fontSize = 14.sp,
                            color = AppColors.TextSecondary
                        )
                        if (!error.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error.orEmpty(),
                                fontSize = 12.sp,
                                color = AppColors.Error
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(comments, key = { it.id }) { comment ->
                        CommentItem(comment = comment)
                    }
                }
            }

            // 底部固定输入行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_gallery),
                    contentDescription = "附件",
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "be the first to comment",
                            color = AppColors.TextHint,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Border,
                        unfocusedBorderColor = AppColors.Border,
                        cursorColor = AppColors.OrangePrimary,
                        focusedContainerColor = AppColors.SurfaceVariant,
                        unfocusedContainerColor = AppColors.SurfaceVariant
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(10.dp))

                // 发送按钮：紫色圆形
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AppColors.PurpleAccent)
                        .clickable {
                            if (inputText.isBlank()) {
                                toast = "请输入评论内容"
                            } else {
                                viewModel.sendComment(inputText)
                                toast = "评论已发送"
                                inputText = ""
                            }
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

            // toast 反馈
            if (!toast.isNullOrEmpty()) {
                Text(
                    text = toast.orEmpty(),
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

/**
 * 单条评论 UI：左侧头像 + 昵称/内容/时间 + 底部互动图标
 */
@Composable
private fun CommentItem(comment: CommentEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        CircleAvatar(
            avatarUrl = comment.authorAvatar,
            name = comment.authorName,
            size = 36.dp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.authorName,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )
            Text(
                text = comment.content,
                fontSize = 14.sp,
                color = AppColors.TextPrimary,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.createdAt.toRelativeTime(),
                    fontSize = 11.sp,
                    color = AppColors.TextSecondary
                )
                Spacer(modifier = Modifier.width(16.dp))

                // 评论图标 + 数量
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_send),
                    contentDescription = "回复",
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = formatCountK(comment.commentCount),
                    fontSize = 11.sp,
                    color = AppColors.TextSecondary
                )
                Spacer(modifier = Modifier.width(16.dp))

                // 点赞图标 + 数量（红色高亮）
                Icon(
                    painter = painterResource(android.R.drawable.ic_media_play),
                    contentDescription = "点赞",
                    tint = AppColors.Error,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = formatCountK(comment.likeCount),
                    fontSize = 11.sp,
                    color = AppColors.Error
                )
            }
        }
    }
}

/**
 * 大数字格式化为 "X.Xk" 或直接返回整数文本
 */
private fun formatCountK(count: Int): String {
    return if (count < 1000) count.toString() else String.format("%.1fk", count / 1000.0)
}
