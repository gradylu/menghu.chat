package menghu.chat.feature.notification.ui

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import menghu.chat.core.ui.component.CircleAvatar
import menghu.chat.core.ui.component.CommonTopBar
import menghu.chat.core.ui.component.SectionHeader
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.feature.notification.model.NotificationEntity
import menghu.chat.feature.notification.viewmodel.NotificationViewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * 通知中心 Screen
 * - 顶部：CommonTopBar（"Notification" 标题）
 * - 主体：LazyColumn，按 Today / Yesterday / This week 分组展示
 * - 每条通知：左侧圆形头像 + 中部（昵称 + 类型文案）+ 右侧（相对时间 + 未读红点）
 * - 支持点击卡片清除（左滑/右滑）
 *
 * @param viewModel ViewModel（Hilt 注入）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Notification",
                onMenuClick = { Timber.d("[NotificationScreen] 菜单点击") },
                onNotificationClick = { Timber.d("[NotificationScreen] 铃铛点击") },
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
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无通知",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )
                }
            } else {
                // 按分组聚合：Today / Yesterday / This week
                val grouped = rememberGrouped(notifications)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    grouped.forEach { (group, list) ->
                        item {
                            SectionHeader(
                                title = group.title,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(list, key = { it.id }) { notification ->
                            NotificationItem(
                                notification = notification,
                                onClear = { viewModel.clearNotification(it.id) }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

/**
 * 通知分组枚举
 */
private enum class NotificationGroup(val title: String) {
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    THIS_WEEK("This week")
}

/**
 * 将通知列表按时间分组（Today / Yesterday / This week），并保持组内按时间倒序
 */
@Composable
private fun rememberGrouped(list: List<NotificationEntity>): Map<NotificationGroup, List<NotificationEntity>> {
    // 这里直接计算（非 State 级别的缓存，适合少量数据）
    val now = System.currentTimeMillis()
    val day = TimeUnit.DAYS.toMillis(1)
    val week = TimeUnit.DAYS.toMillis(7)

    return list
        .sortedByDescending { it.createdAt }
        .groupBy { notification ->
            val diff = now - notification.createdAt
            when {
                diff < day -> NotificationGroup.TODAY
                diff < 2 * day -> NotificationGroup.YESTERDAY
                diff < week -> NotificationGroup.THIS_WEEK
                else -> NotificationGroup.THIS_WEEK
            }
        }
        .toSortedMap(compareBy { it.ordinal })
}

/**
 * 单条通知项：支持左/右滑清除
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationItem(
    notification: NotificationEntity,
    onClear: (NotificationEntity) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                onClear(notification)
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.OrangePrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "清除",
                    color = AppColors.OrangePrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        content = {
            NotificationRow(
                notification = notification,
                onClick = { onClear(notification) }
            )
        }
    )
}

/**
 * 通知行 UI：头像 + 中部文字 + 右侧（时间 + 未读红点）
 */
@Composable
private fun NotificationRow(
    notification: NotificationEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧头像
        CircleAvatar(
            avatarUrl = notification.relatedUserAvatar,
            name = notification.relatedUserName,
            size = 48.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 中部：昵称 + 类型文案
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.relatedUserName,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = notification.content,
                fontSize = 13.sp,
                color = AppColors.TextSecondary,
                maxLines = 2
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 右侧：相对时间 + 未读红点
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = formatRelativeTime(notification.createdAt),
                fontSize = 11.sp,
                color = AppColors.TextSecondary
            )
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AppColors.OrangePrimary)
                )
            }
        }
    }
}

/**
 * 格式化相对时间
 */
private fun formatRelativeTime(epochMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - epochMillis
    val minute = TimeUnit.MINUTES.toMillis(1)
    val hour = TimeUnit.HOURS.toMillis(1)
    val day = TimeUnit.DAYS.toMillis(1)

    return when {
        diff < minute -> "刚刚"
        diff < hour -> "${diff / minute} min ago"
        diff < day -> "${diff / hour} hour ago"
        else -> "${diff / day} day ago"
    }
}
