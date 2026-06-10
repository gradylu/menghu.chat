package menghu.chat.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import menghu.chat.core.ui.theme.AppColors

/**
 * 圆形头像组件
 * 优先显示 avatarUrl 图片；为空时显示首字母占位
 * 可额外带紫色外圈描边
 *
 * @param avatarUrl 头像图片地址（可为空）
 * @param name 用户名（为空时用于生成首字母占位）
 * @param size 头像尺寸
 * @param showPurpleRing 是否显示紫色外环描边
 * @param modifier 自定义修饰符
 */
@Composable
fun CircleAvatar(
    avatarUrl: String? = null,
    name: String = "",
    size: Dp = 48.dp,
    showPurpleRing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val borderModifier = if (showPurpleRing) {
        Modifier.border(
            BorderStroke(2.dp, AppColors.PurpleAccent),
            CircleShape
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .then(borderModifier)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl.isNullOrEmpty()) {
            // 字母占位背景
            Surface(
                color = AppColors.SurfaceVariant,
                modifier = Modifier.size(size)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val firstChar = if (name.isNotEmpty()) {
                        name.first().uppercaseChar().toString()
                    } else {
                        "?"
                    }
                    Text(
                        text = firstChar,
                        fontSize = (size.value / 3).sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextSecondary
                    )
                }
            }
        } else {
            // 使用 Coil 加载网络图片
            AsyncImage(
                model = avatarUrl,
                contentDescription = "头像",
                modifier = Modifier.size(size),
                contentScale = ContentScale.Crop,
                placeholder = null,
                fallback = null
            )
        }
    }
}
