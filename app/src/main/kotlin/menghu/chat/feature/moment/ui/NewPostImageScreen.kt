package menghu.chat.feature.moment.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.navigation.Destinations

/**
 * 发布页第 1 步：图片选择 + 滤镜预览
 * 包含顶部 Tab（Post/Story/Live）、大图预览、滤镜缩略图行、Filter/Edit Tab、画廊网格
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostImageScreen(navController: NavController) {
    // 模拟图库资源（使用 picsum 生成固定占位图）
    val gallerySeeds = remember { (0 until 24).map { "gallery_$it" } }
    val galleryUrls = remember(gallerySeeds) {
        gallerySeeds.map { seed -> "https://picsum.photos/seed/$seed/400/400" }
    }

    // 选中的图片索引（默认选中第 1 张）
    var selectedIndex by remember { mutableIntStateOf(0) }

    // 滤镜名列表（Normal 不应用滤镜，其他使用 ColorMatrix 模拟不同风格）
    val filters = remember {
        listOf(
            "Normal",
            "Mono",
            "Warm",
            "Cool",
            "Sepia",
            "Vintage",
            "Bloom"
        )
    }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    // 顶部 Post/Story/Live Tab
    var activeTab by remember { mutableIntStateOf(0) }

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
                actions = {
                    Text(
                        text = "Next",
                        color = AppColors.OrangePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                // 跳转到发布页第 2 步，携带选中的图片 URL
                                val selectedUrl = galleryUrls.getOrNull(selectedIndex).orEmpty()
                                navController.navigate("${Destinations.NewPostText.route}/$selectedUrl")
                            }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
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
        ) {
            // Post / Story / Live Tab
            PublishTabRow(
                tabs = listOf("Post", "Story", "Live"),
                activeIndex = activeTab,
                onTabClick = { activeTab = it }
            )

            // 大图预览（4:3）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                AsyncImage(
                    model = galleryUrls[selectedIndex],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    colorFilter = buildFilterMatrix(filters[selectedFilterIndex])
                )
            }

            // 滤镜缩略图行
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filters) { filterName ->
                    val idx = filters.indexOf(filterName)
                    FilterThumb(
                        imageUrl = galleryUrls[selectedIndex],
                        filterName = filterName,
                        isSelected = idx == selectedFilterIndex,
                        onClick = { selectedFilterIndex = idx }
                    )
                }
            }

            // Filter / Edit 文字 Tab
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Filter",
                    fontSize = 14.sp,
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(end = 32.dp)
                        .clickable { /* 当前页 */ }
                )
                Text(
                    text = "Edit",
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* 预留编辑 Tab */ }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 画廊网格 + 浮动按钮容器
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    items(galleryUrls) { url ->
                        val idx = galleryUrls.indexOf(url)
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedIndex = idx }
                                .border(
                                    BorderStroke(
                                        if (selectedIndex == idx) 3.dp else 0.dp,
                                        AppColors.OrangePrimary
                                    ),
                                    RoundedCornerShape(10.dp)
                                )
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

                // 右下角浮动相机按钮
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 24.dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(AppColors.OrangePrimary)
                        .clickable { /* 预留拍照点击 */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_camera),
                        contentDescription = "拍照",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // 左下角浮动铅笔按钮
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 24.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(BorderStroke(1.dp, AppColors.Border), CircleShape)
                        .clickable { /* 预留编辑点击 */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_edit),
                        contentDescription = "编辑",
                        tint = AppColors.TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * 发布页顶部 Tab 行
 */
@Composable
private fun PublishTabRow(
    tabs: List<String>,
    activeIndex: Int,
    onTabClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { idx, title ->
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = if (idx == activeIndex) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (idx == activeIndex) AppColors.TextPrimary else AppColors.TextSecondary,
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .clickable { onTabClick(idx) }
                )
            }
        }
        // 下划线指示条：以第一个 Tab 为基准（此处使用简单实现）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            repeat(tabs.size) { idx ->
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (idx == activeIndex) AppColors.OrangePrimary else Color.Transparent
                        )
                )
            }
        }
    }
}

/**
 * 滤镜缩略图：显示带滤镜效果的图片 + 底部文字
 * 选中状态带橙色边框与深色背景
 */
@Composable
private fun FilterThumb(
    imageUrl: String,
    filterName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(if (isSelected) AppColors.SurfaceVariant else Color.Transparent)
            .padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    BorderStroke(
                        if (isSelected) 2.dp else 0.dp,
                        AppColors.OrangePrimary
                    ),
                    RoundedCornerShape(10.dp)
                )
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                colorFilter = buildFilterMatrix(filterName)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = filterName,
            fontSize = 11.sp,
            color = if (isSelected) AppColors.OrangePrimary else AppColors.TextSecondary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

/**
 * 根据滤镜名生成 ColorFilter：
 * - Normal 不应用
 * - Mono 去饱和
 * - Warm 增加红色通道 + 减少蓝色
 * - Cool 增加蓝色 + 减少红色
 * - Sepia 经典怀旧
 * - Vintage 降低对比度 + 轻微偏色
 * - Bloom 增加亮度
 *
 * 返回 null 表示不使用任何滤镜
 */
private fun buildFilterMatrix(filterName: String): ColorFilter? {
    val matrix = when (filterName) {
        "Mono" -> ColorMatrix(
            floatArrayOf(
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        "Warm" -> ColorMatrix(
            floatArrayOf(
                1.1f, 0f, 0f, 0f, 10f,
                0f, 1.0f, 0f, 0f, 5f,
                0f, 0f, 0.85f, 0f, -15f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        "Cool" -> ColorMatrix(
            floatArrayOf(
                0.9f, 0f, 0f, 0f, -10f,
                0f, 0.95f, 0f, 0f, 0f,
                0f, 0f, 1.15f, 0f, 15f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        "Sepia" -> ColorMatrix(
            floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        "Vintage" -> ColorMatrix(
            floatArrayOf(
                0.9f, 0.1f, 0.1f, 0f, -20f,
                0.1f, 0.85f, 0.1f, 0f, -10f,
                0.1f, 0.1f, 0.75f, 0f, -30f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        "Bloom" -> ColorMatrix(
            floatArrayOf(
                1.15f, 0f, 0f, 0f, 20f,
                0f, 1.1f, 0f, 0f, 15f,
                0f, 0f, 1.1f, 0f, 15f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        else -> null
    } ?: return null
    return ColorFilter.colorMatrix(matrix)
}
