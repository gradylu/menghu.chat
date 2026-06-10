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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import menghu.chat.core.ui.component.CircleAvatar
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.core.utils.TimeUtils
import menghu.chat.feature.chat.model.MessageEntity
import menghu.chat.feature.chat.viewmodel.ChatViewModel
import menghu.chat.navigation.Destinations
import timber.log.Timber

/**
 * 单聊页面 Screen
 * - 顶部：返回 + 头像 + 昵称 + 在线状态 + 电话/视频图标
 * - 中部：消息气泡列表（自己在右橙色、对方在左白色；还有"正在输入"气泡
 * - 底部：附件图标 + 输入框 + 紫色发送按钮
 *
 * @param conversationId 会话 ID（从 NavHost 显式传入，作为 SavedStateHandle 的兜底方案
 */
@Composable
fun ChatScreen(
    navController: NavController,
    conversationId: Long = 0L,
    viewModel: ChatViewModel = hiltViewModel()
) {
    // 🛡️ 双路径保障：若外部显式传入了有效 ID，用它覆盖/补充 SavedStateHandle 的解析结果
    // 使用 LaunchedEffect 而不是直接在 composition 中调用，符合 Compose 规则
    LaunchedEffect(conversationId) {
        if (conversationId > 0) {
            Timber.d("[ChatScreen] LaunchedEffect: 通知 ViewModel 使用 conversationId=%s", conversationId)
            viewModel.ensureConversationId(conversationId)
        }
    }

    val conversation by viewModel.conversation.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val isTyping by viewModel.isTyping.collectAsStateWithLifecycle()

    // 附件选择弹窗是否显示（由 UI 层维护）
    var showAttachment by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ChatTopBar(
                title = conversation?.peerName ?: "Chat",
                online = conversation?.peerOnline == true,
                avatarUrl = conversation?.peerAvatar,
                onBack = {
                    Timber.d("[ChatScreen] 点击返回")
                    navController.popBackStack()
                },
                onVideoCall = {
                    Timber.d("[ChatScreen] 点击视频通话，跳转到 VideoCall")
                    val userId = conversation?.peerId ?: 0L
                    navController.navigate(Destinations.VideoCall.withUserId(userId))
                }
            )
        },
        containerColor = AppColors.Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 消息列表（可滚动）
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false
            ) {
                // 正常消息气泡
                items(
                    items = messages,
                    key = { it.id }
                ) { msg ->
                    MessageBubble(message = msg)
                }

                // "对方正在输入"气泡（仅当 isTyping = true 时显示
                if (isTyping) {
                    item {
                        TypingBubble()
                    }
                }
                // 底部留白
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // 输入栏
            ChatInputBar(
                inputText = inputText,
                onInputChange = viewModel::onInputChanged,
                onSend = viewModel::sendMessage,
                onAttachment = { showAttachment = true }
            )
        }
    }

    // 附件选择弹窗（下半屏，底部与屏幕底边对齐）
    if (showAttachment) {
        // 动态计算高度 = 屏幕高度的 50%
        val configuration = LocalConfiguration.current
        val halfScreenHeight = (configuration.screenHeightDp * 0.5f).dp

        Dialog(
            onDismissRequest = { showAttachment = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,  // 完全自定义尺寸
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            // 最外层：占满 Dialog 空间，内容从底部起
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // 实际内容：贴底部，宽度撑满，高度最多半屏
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 0.dp, max = halfScreenHeight)
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(AppColors.Background)
                ) {
                    // 顶部抓手指示条
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(AppColors.TextSecondary.copy(alpha = 0.3f))
                            .align(Alignment.TopCenter)
                    )
                    // 九宫格选项
                    AttachmentBottomSheet(
                        onDismiss = { showAttachment = false },
                        onSelected = { optionName ->
                            showAttachment = false
                            Timber.d("[ChatScreen] 用户选择附件：%s", optionName)
                        }
                    )
                }
            }
        }
    }
}

/** 单聊页顶部栏
 */
@Composable
private fun ChatTopBar(
    title: String,
    online: Boolean,
    avatarUrl: String?,
    onBack: () -> Unit,
    onVideoCall: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Background)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = AppColors.TextPrimary
            )
        }

        // 头像
        CircleAvatar(
            avatarUrl = avatarUrl,
            name = title,
            size = 40.dp,
            showPurpleRing = true
        )
        Spacer(modifier = Modifier.width(8.dp))

        // 昵称 + 在线状态
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary,
                fontSize = 16.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (online) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AppColors.Success)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Online",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary,
                        fontSize = 12.sp
                    )
                } else {
                    Text(
                        text = "Offline",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextHint,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 电话
        IconButton(onClick = { Timber.d("[ChatScreen] 点击电话（占位）") }) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "电话",
                tint = AppColors.PurpleAccent
            )
        }
        // 视频
        IconButton(onClick = onVideoCall) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "视频通话",
                tint = AppColors.PurpleAccent
            )
        }
    }
}

/** 单个消息气泡
 * - isMine=true：右侧橙色圆角气泡；isMine=false：左侧白色圆角气泡
 * - 底部显示发送时间
 */
@Composable
private fun MessageBubble(message: MessageEntity) {
    val isMine = message.isMine
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        // 气泡主体
        val bubbleColor = if (isMine) AppColors.OrangePrimary else Color.White
        val textColor = if (isMine) Color.White else AppColors.TextPrimary

        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMine) 16.dp else 4.dp,
                bottomEnd = if (isMine) 4.dp else 16.dp
            ),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = TimeUtils.formatRelativeTime(message.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isMine) Color.White.copy(alpha = 0.8f) else AppColors.TextHint,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/** 对方正在输入气泡（简化的 "•••" 样式
 */
@Composable
private fun TypingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 4.dp,
                bottomEnd = 16.dp
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = "•••",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextSecondary,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                letterSpacing = 4.sp
            )
        }
    }
}

/** 底部输入栏（附件图标 + 输入框 + 发送按钮
 */
@Composable
private fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttachment: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Background)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 附件
        IconButton(onClick = onAttachment) {
            Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = "附件",
                tint = AppColors.PurpleAccent
            )
        }

        // 输入框
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            placeholder = {
                Text(
                    text = "I will wait fo...",
                    color = AppColors.TextHint,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            textStyle = TextStyle(fontSize = 14.sp, color = AppColors.TextPrimary),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.PurpleAccent,
                unfocusedBorderColor = AppColors.Border,
                cursorColor = AppColors.PurpleAccent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 发送按钮（圆形紫色填充
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AppColors.PurpleAccent)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "发送",
                tint = Color.White
            )
        }
    }
}
