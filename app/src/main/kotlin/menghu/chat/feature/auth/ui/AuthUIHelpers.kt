package menghu.chat.feature.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import menghu.chat.core.ui.theme.AppColors

/**
 * 登录/注册页通用：顶部渐变橙色装饰背景（含圆形/椭圆）
 */
@Composable
internal fun AuthHeaderDecor() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(AppColors.OrangePrimary, AppColors.OrangeLight),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(320.dp)) {
            // 右上角大圆
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = 140.dp.toPx(),
                center = Offset(size.width - 40.dp.toPx(), 40.dp.toPx())
            )
            // 左上角小圆
            drawCircle(
                color = Color.White.copy(alpha = 0.12f),
                radius = 60.dp.toPx(),
                center = Offset(40.dp.toPx(), 80.dp.toPx())
            )
            // 中部椭圆
            drawOval(
                color = Color.White.copy(alpha = 0.06f),
                size = androidx.compose.ui.geometry.Size(
                    220.dp.toPx(),
                    120.dp.toPx()
                ),
                topLeft = Offset(size.width - 200.dp.toPx(), 180.dp.toPx())
            )
        }
    }
}

/**
 * 登录/注册页通用：带文本标签的输入框包装
 */
@Composable
internal fun LabeledField(
    label: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = AppColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

/**
 * 登录/注册页通用：三方登录占位按钮（白底 + 彩色图标）
 */
@Composable
internal fun SocialIconButton(
    icon: @Composable () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = { /* 三方登录占位：后续接入具体 SDK */ },
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AppColors.Border),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = AppColors.TextPrimary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            icon()
            Text(text = label, fontSize = 12.sp)
        }
    }
}
