package menghu.chat.feature.chat.ui

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import menghu.chat.core.ui.component.CircleAvatar
import menghu.chat.core.ui.component.CommonTopBar
import menghu.chat.core.ui.component.SearchBar
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.core.utils.TimeUtils
import menghu.chat.feature.chat.model.ConversationEntity
import menghu.chat.feature.chat.viewmodel.MessagesViewModel
import menghu.chat.navigation.Destinations
import timber.log.Timber

/**
 * 消息列表（会话列表）Screen
 * - 顶部：标题 "Messages" + 搜索框
 * - 列表：会话项（头像 + 昵称/消息预览 + 相对时间/未读徽标）
 * - 点击某项跳转到单聊页面（Destinations.Chat）
 */
@Composable
fun MessagesScreen(
    navController: NavController,
    viewModel: MessagesViewModel = hiltViewModel()
) {
    // 通过 collectAsStateWithLifecycle 订阅状态（生命周期感知，避免后台不必要重组）
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val keyword by viewModel.keyword.collectAsStateWithLifecycle()

    // 按关键字过滤会话（仅在 UI 层做简单显示过滤，不修改数据源）
    val displayed = rememberFilteredList(conversations, keyword)

    Scaffold(
        // ✅ 顶部：共享 CommonTopBar（标题居中 + 橙色标题），下方加搜索栏
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                CommonTopBar(
                    title = "Messages",
                    onMenuClick = {},
                    onNotificationClick = { navController.navigate(Destinations.Notification.route) }
                )
                SearchBar(
                    value = keyword,
                    onValueChange = viewModel::onKeywordChanged,
                    placeholder = "搜索联系人/消息…",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        },
        containerColor = AppColors.Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    // 加载中：居中进度圈
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(40.dp),
                        color = AppColors.PurpleAccent
                    )
                }

                error != null -> {
                    // 错误：居中提示（Mock 场景实际不会走到这里）
                    Text(
                        text = "加载失败：${error}",
                        color = AppColors.Error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                displayed.isEmpty() -> {
                    // 空状态：居中提示
                    Text(
                        text = "暂无会话",
                        color = AppColors.TextSecondary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    // 正常列表
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(
                            items = displayed,
                            key = { it.id }
                        ) { conv ->
                            ConversationItem(
                                conversation = conv,
                                onClick = {
                                    Timber.d("[MessagesScreen] 点击会话 %s，跳转到 Chat", conv.id)
                                    navController.navigate(
                                        Destinations.Chat.withConversationId(conv.id)
                                    )
                                }
                            )
                        }
                        // 底部留白，避免被导航栏遮挡
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

/** 根据关键字过滤会话列表的简单帮助函数（纯 Composable 层，不影响数据源）
 */
@Composable
private fun rememberFilteredList(
    list: List<ConversationEntity>,
    keyword: String
): List<ConversationEntity> {
    // 简单实现：无状态，直接返回过滤结果（列表数量极少，无需额外缓存）
    return if (keyword.isBlank()) {
        list
    } else {
        val k = keyword.trim().lowercase()
        list.filter {
            it.peerName.lowercase().contains(k) ||
                it.lastMessage.lowercase().contains(k)
        }
    }
}

/** 单条会话项组件
 * - 左侧：圆形头像（紫色描边，在线显示绿色小圆点）
 * - 中间：昵称 + 消息预览（单行省略）
 * - 右侧：相对时间 + 未读徽标
 */
@Composable
private fun ConversationItem(
    conversation: ConversationEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像 + 在线状态
        Box {
            CircleAvatar(
                avatarUrl = conversation.peerAvatar,
                name = conversation.peerName,
                size = 52.dp,
                showPurpleRing = true
            )
            if (conversation.peerOnline) {
                // 在线状态：绿色小圆点（右下角）
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(AppColors.Success)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 中间：昵称 + 预览
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.peerName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary,
                fontSize = 16.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = conversation.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 右侧：相对时间 + 未读徽标
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = TimeUtils.formatRelativeTime(conversation.lastMessageAt),
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary,
                fontSize = 12.sp
            )
            if (conversation.unreadCount > 0) {
                // 未读数徽标（紫色背景，白字）
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(AppColors.PurpleAccent)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.unreadCount.coerceAtMost(99).let {
                            if (it >= 100) "99+" else it.toString()
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
