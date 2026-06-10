package menghu.chat.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material3 形状规范
 * 统一圆角定义：小 8dp / 中 12dp / 大 16dp / 超大 24dp
 */
val AppShapes = Shapes(
    // 小卡片、标签等
    small = RoundedCornerShape(8.dp),
    // 按钮、输入框、卡片等
    medium = RoundedCornerShape(12.dp),
    // 大卡片、对话框、底部 Sheet
    large = RoundedCornerShape(16.dp),
    // 额外：24dp
    extraLarge = RoundedCornerShape(24.dp)
)

/** 12dp 圆角（按钮、卡片常用） */
val RadiusMedium = RoundedCornerShape(12.dp)

/** 16dp 圆角 */
val RadiusLarge = RoundedCornerShape(16.dp)

/** 24dp 圆角 */
val RadiusXLarge = RoundedCornerShape(24.dp)

/** 完全圆形 */
val RadiusFull = RoundedCornerShape(percent = 50)
