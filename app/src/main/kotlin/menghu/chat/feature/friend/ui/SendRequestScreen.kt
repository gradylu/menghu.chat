package menghu.chat.feature.friend.ui

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.feature.friend.model.NearbyUserEntity
import menghu.chat.feature.friend.viewmodel.SendRequestViewModel
import timber.log.Timber

/**
 * 发送好友请求页
 * - 顶部：返回按钮
 * - 全屏深色渐变蒙层 + 背景大图（头像模糊背景）
 * - 中央：圆形大头像 + 姓名 + 距离
 * - 底部橙色大按钮："Send friend request"，点击后 Toast 提示，1 秒后返回上一页
 *
 * @param navController 路由控制器
 * @param userId 目标用户 ID
 */
@Composable
fun SendRequestScreen(
    navController: NavController,
    userId: Long
) {
    val viewModel: SendRequestViewModel = hiltViewModel()
    LaunchedEffect(Unit) { viewModel.loadUsers() }

    val users by viewModel.users.collectAsStateWithLifecycle()
    val user: NearbyUserEntity = remember(users, userId) {
        users.firstOrNull { it.userId == userId }
            ?: NearbyUserEntity(
                userId = userId,
                name = "Andrew Ruth",
                avatar = "https://picsum.photos/seed/send_req_$userId/800/800",
                distanceKm = 13.0
            )
    }

    var sent by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(sent) {
        if (sent) {
            Timber.d("[SendRequest] 好友请求已发送，延迟 1 秒后返回")
            delay(1000L)
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ===== 背景大图（模糊）=====
        AsyncImage(
            model = user.avatar,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 24.dp),
            contentScale = ContentScale.Crop,
            alpha = 0.7f
        )

        // ===== 深色渐变蒙层 =====
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // ===== 内容列 =====
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部：返回按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))

            // 圆形大头像
            AsyncImage(
                model = user.avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 姓名
            Text(
                text = user.name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 距离
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = AppColors.OrangePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatDistance(user.distanceKm),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }

            // 底部发送按钮
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    Toast.makeText(context, "已发送好友请求", Toast.LENGTH_SHORT).show()
                    sent = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.OrangePrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Send friend request",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/** 格式化距离：整数显示 Nkm，否则保留 1 位小数 */
private fun formatDistance(km: Double): String {
    return if (km % 1.0 == 0.0) "${km.toInt()}km" else "%.1fkm".format(km)
}
