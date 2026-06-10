package menghu.chat.feature.chat.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import menghu.chat.core.ui.component.CircleAvatar
import menghu.chat.core.ui.theme.AppColors
import timber.log.Timber

/**
 * 视频通话占位页（不做真实 RTC
 * - 全屏深色背景
 * - 上方：对方视频画面大字 + 头像
 * - 下方：麦克风、扬声器、摄像头、聊天、红色挂断按钮
 * - 点击挂断：popBackStack 返回聊天页
 */
@Composable
fun VideoCallScreen(navController: NavController) {
    Scaffold(
        containerColor = Color(0xFF1A1A2E)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF1A1A2E))
        ) {
            // 上半部分：对方视频画面占位
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "对方视频画面",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    CircleAvatar(
                        avatarUrl = null,
                        name = "Chat Partner",
                        size = 120.dp,
                        showPurpleRing = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Chat Partner",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "通话中 00:00",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }

            // 下半部分：操作栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                VideoActionButton(
                    onClick = { Timber.d("[VideoCallScreen] 麦克风（占位）") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "麦克风",
                            tint = Color.White
                        )
                    }
                )
                VideoActionButton(
                    onClick = { Timber.d("[VideoCallScreen] 扬声器（占位）") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "扬声器",
                            tint = Color.White
                        )
                    }
                )
                VideoActionButton(
                    onClick = { Timber.d("[VideoCallScreen] 摄像头（占位）") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "摄像头",
                            tint = Color.White
                        )
                    }
                )
                VideoActionButton(
                    onClick = { Timber.d("[VideoCallScreen] 聊天（占位）") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "聊天",
                            tint = Color.White
                        )
                    }
                )
                // 红色挂断按钮
                IconButton(
                    onClick = {
                        Timber.d("[VideoCallScreen] 挂断，返回上一页")
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "挂断",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

/** 视频通话操作按钮
 */
@Composable
private fun VideoActionButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f))
    ) {
        icon()
    }
}
