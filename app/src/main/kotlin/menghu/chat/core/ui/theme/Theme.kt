package menghu.chat.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * 浅色主题配色方案
 * 以橙色为主色、紫色为强调色
 */
private val LightColorScheme = lightColorScheme(
    primary = AppColors.OrangePrimary,
    onPrimary = AppColors.TextOnPrimary,
    primaryContainer = AppColors.OrangeLight,
    onPrimaryContainer = AppColors.TextPrimary,
    secondary = AppColors.PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = AppColors.PurpleLight,
    onSecondaryContainer = AppColors.TextPrimary,
    tertiary = AppColors.Success,
    background = AppColors.Background,
    onBackground = AppColors.TextPrimary,
    surface = AppColors.Surface,
    onSurface = AppColors.TextPrimary,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.TextSecondary,
    error = AppColors.Error,
    onError = Color.White,
    outline = AppColors.Border
)

/**
 * 深色主题配色方案
 * 简化实现，目前与浅色共享部分颜色
 */
private val DarkColorScheme = darkColorScheme(
    primary = AppColors.OrangeLight,
    secondary = AppColors.PurpleLight,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = AppColors.TextPrimary,
    onSecondary = AppColors.TextPrimary,
    onBackground = Color.White,
    onSurface = Color.White,
    error = AppColors.Error
)

/**
 * 梦狐社交全局主题
 * 包装 MaterialTheme，对外提供统一入口
 *
 * @param darkTheme 是否为深色模式；默认跟随系统
 * @param dynamicColor 是否启用 Android 12+ 动态取色；默认关闭
 */
@Composable
fun MenghuChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // 同步状态栏颜色
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
