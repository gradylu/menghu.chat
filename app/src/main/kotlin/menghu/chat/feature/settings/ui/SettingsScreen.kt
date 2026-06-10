package menghu.chat.feature.settings.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import menghu.chat.core.ui.component.CircleAvatar
import menghu.chat.core.ui.component.CommonTopBar
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.feature.settings.viewmodel.SettingsViewModel
import timber.log.Timber

/**
 * 设置页 Screen
 * - 顶部：CommonTopBar（标题 "Settings"
 * - 头部用户信息行：头像 + 昵称 + 邮箱
 * - 选项组（Account / Chat / New Group / Security / Notification / Help
 * - 底部大按钮 Log out（橙色描边
 * - 点击任一项 → Toast 提示（Mock），点击 Logout → 登出
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit = {}
) {
    val logoutSuccess by viewModel.logoutResult.collectAsStateWithLifecycle()

    // 若登出完成 → 回调上层由调用者处理（使用 LaunchedEffect 避免在重组过程中触发导航
    LaunchedEffect(logoutSuccess) {
        if (logoutSuccess) {
            onLogoutSuccess()
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Settings",
                onMenuClick = { Timber.d("[SettingsScreen] 菜单点击") },
                onNotificationClick = { Timber.d("[SettingsScreen] 铃铛点击") },
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
            // 头部用户信息
            UserProfileRow(
                avatar = "https://picsum.photos/seed/musiani/160/160",
                name = "Musiani Wanda",
                email = "musiani.wanda@example.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 选项组
            val items = listOf(
                SettingItem("Account", "Privacy, Change Number"),
                SettingItem("Chat", "Theme, wallpaper, chat history"),
                SettingItem("New Group", "Create Group from Contacts"),
                SettingItem("Security", "Change Password"),
                SettingItem("Notification", "Message, group, ringtone"),
                SettingItem("Help", "Help center, contact us, privacy policy")
            )

            items.forEach { item ->
                SettingRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = {
                        Timber.d("[SettingsScreen] 进入 ${item.title} 设置")
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 底部 Log out 按钮
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    border = BorderStroke(1.5.dp, AppColors.OrangePrimary),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = AppColors.OrangePrimary
                    )
                ) {
                    Text(
                        text = "Log out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * 用户信息行：头像 + 昵称 + 邮箱
 */
@Composable
private fun UserProfileRow(
    avatar: String,
    name: String,
    email: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleAvatar(
            avatarUrl = avatar,
            name = name,
            size = 56.dp
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = email,
                fontSize = 13.sp,
                color = AppColors.TextSecondary
            )
        }
    }
}

/**
 * 单个设置行：图标 + 标题 + 副标题 + 箭头
 */
@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧圆角图标容器（用首字母作为占位图标
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.OrangePrimary.copy(alpha = 0.12f)),
            modifier = Modifier.size(40.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = title.first().toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.OrangePrimary
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = AppColors.TextSecondary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_more),
            contentDescription = "arrow-right",
            tint = AppColors.TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * 设置项数据模型
 */
private data class SettingItem(val title: String, val subtitle: String)
