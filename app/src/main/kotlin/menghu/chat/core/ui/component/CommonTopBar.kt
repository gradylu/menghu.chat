package menghu.chat.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import menghu.chat.core.ui.theme.AppColors

/**
 * 通用顶部标题栏
 * - 左侧：菜单按钮（三条横线 Icon）
 * - 中间：橙色标题文字
 * - 右侧：铃铛通知图标（若 hasUnreadNotification=true 时，右下角带红点徽章）
 * - 背景白色，底部无阴影或极浅阴影
 *
 * @param title 标题文字
 * @param onMenuClick 左侧菜单点击回调
 * @param onNotificationClick 右侧铃铛点击回调
 * @param hasUnreadNotification 是否存在未读通知（红点显示）
 */
@Composable
fun CommonTopBar(
    title: String,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    hasUnreadNotification: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧菜单按钮
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "菜单",
            tint = AppColors.TextPrimary,
            modifier = Modifier
                .size(28.dp)
                .clickable { onMenuClick() }
        )

        // 居中标题
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = title,
            color = AppColors.OrangePrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.weight(1f))

        // 右侧通知铃铛 + 红点
        Box(
            modifier = Modifier
                .size(28.dp)
                .clickable { onNotificationClick() }
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "通知",
                tint = AppColors.TextPrimary,
                modifier = Modifier.size(28.dp)
            )
            if (hasUnreadNotification) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .background(AppColors.Error, CircleShape)
                )
            }
        }
    }
}
