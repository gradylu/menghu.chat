package menghu.chat.core.ui.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.core.utils.toImageList
import menghu.chat.core.utils.toRelativeTime
import menghu.chat.feature.moment.model.PostEntity

/**
 * 公共动态卡片组件
 * - 作者头信息 + 正文 + 多图展示 + 底部互动区
 * 可在 Feed / Profile / Comments 等任意场景复用
 *
 * @param post 动态实体
 * @param onCommentsClick 点击"查看评论"回调（可为空，表示只读展示）
 */
@Composable
fun PostCard(
    post: PostEntity,
    modifier: Modifier = Modifier,
    onCommentsClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 顶部作者信息行：头像 + 昵称 + 时间 + 更多
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleAvatar(
                    avatarUrl = post.authorAvatar,
                    name = post.authorName,
                    size = 40.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = post.createdAt.toRelativeTime(),
                        fontSize = 12.sp,
                        color = AppColors.TextSecondary
                    )
                }
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_more),
                    contentDescription = "更多",
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 正文文本
            if (post.content.isNotEmpty()) {
                Text(
                    text = post.content,
                    fontSize = 14.sp,
                    color = AppColors.TextPrimary,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 图片区：根据图片数量展示不同布局
            val imageList = post.images.toImageList()
            if (imageList.isNotEmpty()) {
                PostImageGrid(images = imageList)
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 底部互动区：评论数 + 点赞数 + 查看评论
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_send),
                    contentDescription = "评论",
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatCount(post.commentCount),
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary
                )
                Spacer(modifier = Modifier.width(20.dp))
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_view),
                    contentDescription = "点赞",
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatCount(post.likeCount),
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                if (onCommentsClick != null) {
                    Text(
                        text = "查看评论",
                        fontSize = 12.sp,
                        color = AppColors.OrangePrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onCommentsClick() }
                    )
                }
            }
        }
    }
}

/**
 * 动态图片网格：
 * - 1 张：单张大图
 * - 2 张：横向等宽 2 图
 * - 3 张：左边大图 + 右侧上下 2 张小图
 * - 其他：最多显示 4 图 2x2 网格，最后一张带 "+N" 角标
 */
@Composable
private fun PostImageGrid(images: List<String>) {
    when {
        images.size == 1 -> {
            AsyncImage(
                model = images.first(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
        images.size == 2 -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                images.take(2).forEach { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        images.size == 3 -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AsyncImage(
                    model = images[0],
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AsyncImage(
                        model = images[1],
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    AsyncImage(
                        model = images[2],
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        images.size >= 4 -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(2) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(2) { col ->
                            val index = row * 2 + col
                            val url = images.getOrNull(index).orEmpty()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                if (index == 3 && images.size > 4) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.Black.copy(alpha = 0.45f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+${images.size - 4}",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 数字格式化（小于 1000 显示数字；否则显示 Xk 友好文本）
 */
private fun formatCount(count: Int): String {
    return when {
        count < 1000 -> count.toString()
        count < 10_000 -> {
            val d = count / 1000.0
            if ((d * 10).toInt() % 10 == 0) "${d.toInt()}k"
            else "%.1fk".format(d)
        }
        else -> "${count / 1000}k"
    }
}
