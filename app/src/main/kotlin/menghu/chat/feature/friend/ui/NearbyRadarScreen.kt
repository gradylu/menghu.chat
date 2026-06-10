package menghu.chat.feature.friend.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.navigation.Destinations
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

/**
 * 附近的人雷达页（Nearby Radar）
 * - 全屏橙色渐变背景（OrangePrimary → OrangeLight）
 * - 中央三层同心圆 + 蓝色"查找中"中心图标
 * - 环形排布 6 个圆形头像（点击均跳转到附近用户列表）
 * - 底部中央标题 "People Nearby"，副标题 "Searching people nearby..."
 * - 点击空白或头像均跳转到 NearbyList 页
 *
 * @param navController 路由控制器
 */
@Composable
fun NearbyRadarScreen(navController: NavController) {
    val avatarUrls = (1..6).map { "https://picsum.photos/seed/nearby_radar_$it/200/200" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(AppColors.OrangePrimary, AppColors.OrangeLight)
                )
            )
            .clickable {
                Timber.d("[NearbyRadarScreen] 点击空白跳转到附近用户列表")
                navController.navigate(Destinations.NearbyList.route)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部标题
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Nearby",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            // 雷达区域
            Spacer(modifier = Modifier.height(60.dp))
            RadarRing(
                modifier = Modifier.size(320.dp),
                avatarUrls = avatarUrls,
                onAvatarClick = {
                    navController.navigate(Destinations.NearbyList.route)
                }
            )

            // 底部标题与副标题
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "People Nearby",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Searching people nearby...",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * 雷达环形区域：
 * - Canvas 绘制三层同心圆 + 中心实心圆
 * - 中心叠加白色圆形背景 + 紫色定位图标
 * - 环形均匀排布 6 个圆形头像（使用自定义 Layout 根据角度放置）
 */
@Composable
private fun RadarRing(
    modifier: Modifier = Modifier,
    avatarUrls: List<String>,
    onAvatarClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // ===== 同心圆 Canvas =====
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            (1..3).forEach { i ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.25f + i * 0.12f),
                    radius = i * size.minDimension / 6f,
                    center = center,
                    style = Stroke(width = 1.5f * density)
                )
            }
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = 26f * density,
                center = center
            )
        }

        // ===== 中心图标 =====
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationSearching,
                contentDescription = "查找中",
                tint = AppColors.PurpleAccent,
                modifier = Modifier.size(32.dp)
            )
        }

        // ===== 环形排布的头像（自定义 Layout）=====
        RingLayout(
            modifier = Modifier.fillMaxSize(),
            radiusRatio = 0.42f
        ) {
            avatarUrls.forEach { url ->
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onAvatarClick() }
                        .padding(3.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

/**
 * 环形自定义 Layout：
 * - 取父容器可用尺寸作为 square（取宽高较小值）
 * - 以中心为圆心、半径 = radiusRatio * square / 2 排列子元素
 * - 第一个位于顶部（角度 -90°）
 */
@Composable
private fun RingLayout(
    modifier: Modifier = Modifier,
    radiusRatio: Float = 0.42f,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        // 取可用的最小尺寸，保证是一个正方形
        val size = minOf(constraints.maxWidth, constraints.maxHeight)
        val childConstraints = androidx.compose.ui.unit.Constraints()
        val placeables = measurables.map { it.measure(childConstraints) }

        layout(size, size) {
            val centerX = size / 2
            val centerY = size / 2
            val radius = (size * radiusRatio).toInt()
            val total = placeables.size
            placeables.forEachIndexed { index, placeable ->
                val angleDeg = -90f + (index * 360f / total)
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val x = centerX + (radius * cos(angleRad)).toInt() - placeable.width / 2
                val y = centerY + (radius * sin(angleRad)).toInt() - placeable.height / 2
                placeable.placeRelative(x, y)
            }
        }
    }
}
