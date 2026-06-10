package menghu.chat.feature.moment.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import menghu.chat.core.ui.component.PrimaryButton
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.feature.moment.viewmodel.NewPostViewModel
import menghu.chat.navigation.Destinations

/**
 * 发布页第 2 步：正文描述 + 发布
 * 路由参数为 imageUrl（若用户在上一步选择了图片则携带图片）
 *
 * @param navController 路由控制器
 * @param imageUrl 预选中的图片 URL（可为空或空字符串表示无图）
 * @param viewModel 发布 ViewModel（Hilt 注入）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostTextScreen(
    navController: NavController,
    imageUrl: String? = null,
    viewModel: NewPostViewModel = hiltViewModel()
) {
    val images by viewModel.images.collectAsStateWithLifecycle()
    val isPublishing by viewModel.isPublishing.collectAsStateWithLifecycle()
    val publishSuccess by viewModel.publishSuccess.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var content by remember { mutableStateOf("") }
    var shareFacebook by remember { mutableStateOf(false) }
    var shareInstagram by remember { mutableStateOf(true) }
    var shareTwitter by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    // 页面初始化：若携带了图片 URL 则追加到选中列表
    LaunchedEffect(imageUrl) {
        if (!imageUrl.isNullOrBlank() && images.isEmpty()) {
            viewModel.setSelectedImages(listOf(imageUrl))
        }
    }

    // 发布成功 → 返回 Feeds 页
    LaunchedEffect(publishSuccess) {
        if (publishSuccess) {
            toast = "发布成功"
            viewModel.resetPublishSuccess()
            navController.navigate(Destinations.Feed.route) {
                popUpTo(Destinations.Feed.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New post",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_media_previous),
                        contentDescription = "返回",
                        tint = AppColors.TextPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { navController.popBackStack() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Background,
                    titleContentColor = AppColors.TextPrimary
                )
            )
        },
        containerColor = AppColors.Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 顶部图片预览行（可追加）
            if (images.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(images) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    BorderStroke(1.dp, AppColors.Border),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    toast = "已追加一张占位图"
                                    viewModel.appendImage(
                                        "https://picsum.photos/seed/new_${System.currentTimeMillis()}/400/400"
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.OrangePrimary
                            )
                        }
                    }
                }
            }

            // 正文输入框
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = {
                        Text(
                            text = "What you want to say?",
                            color = AppColors.TextHint,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.OrangePrimary,
                        unfocusedBorderColor = AppColors.Border,
                        cursorColor = AppColors.OrangePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = AppColors.TextPrimary
                    ),
                    singleLine = false,
                    maxLines = 8
                )
            }

            // 选项行：Tag people / Add location / Add music / Feeling
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                OptionRow(title = "Tag people") { toast = "Tag people clicked" }
                OptionRow(title = "Add location") { toast = "Add location clicked" }
                OptionRow(title = "Add music") { toast = "Add music clicked" }
                OptionRow(title = "Feeling or activity") { toast = "Feeling or activity clicked" }
            }

            // Share to 社交平台开关
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Share to",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                ShareRow(
                    title = "Facebook",
                    checked = shareFacebook,
                    onCheckedChange = { shareFacebook = it }
                )
                ShareRow(
                    title = "Instagram",
                    checked = shareInstagram,
                    onCheckedChange = { shareInstagram = it }
                )
                ShareRow(
                    title = "Twitter",
                    checked = shareTwitter,
                    onCheckedChange = { shareTwitter = it }
                )
            }

            // 底部错误提示
            if (!error.isNullOrEmpty()) {
                Text(
                    text = error.orEmpty(),
                    color = AppColors.Error,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // 发布按钮
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = "Post Now",
                onClick = { viewModel.publishPost(content) },
                isLoading = isPublishing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 轻量 toast（Composable 内简易实现）
            if (!toast.isNullOrEmpty()) {
                Text(
                    text = toast.orEmpty(),
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

/**
 * 普通选项行：左侧图标 + 标题 + 右侧箭头
 */
@Composable
private fun OptionRow(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_gallery),
            contentDescription = title,
            tint = AppColors.OrangePrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            color = AppColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(android.R.drawable.ic_media_next),
            contentDescription = "前往",
            tint = AppColors.TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * 分享开关行：平台名 + Switch
 */
@Composable
private fun ShareRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = AppColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppColors.OrangePrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = AppColors.Border
            )
        )
    }
}

/**
 * 给 Column/Row 提供背景颜色方法（避免引入额外扩展依赖，复用 background）
 * 此文件内部直接使用 Compose 标准 background(color) Modifier，无需额外定义扩展。
 */
