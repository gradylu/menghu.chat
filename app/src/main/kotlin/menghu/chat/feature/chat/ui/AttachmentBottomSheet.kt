package menghu.chat.feature.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import menghu.chat.core.ui.theme.AppColors
import timber.log.Timber

/**
 * 附件选择弹窗（2 行 × 3 列九宫格
 * - 顶部标题 "Select Option" + 关闭按钮
 * - Camera（紫）、Galery（橙）、File（紫）
 * - Location（绿）、Audio（红）、Contact（橙）
 * - 点击任意项 → 关闭弹窗并回调选项名
 */
@Composable
fun AttachmentBottomSheet(
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 顶部：标题 + 关闭
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select Option",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = AppColors.TextSecondary
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // 第 1 行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AttachmentCell(
                name = "Camera",
                bgColor = AppColors.PurpleAccent,
                icon = Icons.Default.CameraAlt,
                onClick = {
                    Timber.d("[AttachmentBottomSheet] 选择 Camera")
                    onSelected("Camera")
                }
            )
            AttachmentCell(
                name = "Galery",
                bgColor = AppColors.OrangePrimary,
                icon = Icons.Default.Image,
                onClick = {
                    Timber.d("[AttachmentBottomSheet] 选择 Galery")
                    onSelected("Galery")
                }
            )
            AttachmentCell(
                name = "File",
                bgColor = AppColors.PurpleAccent,
                icon = Icons.Default.AttachFile,
                onClick = {
                    Timber.d("[AttachmentBottomSheet] 选择 File")
                    onSelected("File")
                }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        // 第 2 行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AttachmentCell(
                name = "Location",
                bgColor = AppColors.Success,
                icon = Icons.Default.Place,
                onClick = {
                    Timber.d("[AttachmentBottomSheet] 选择 Location")
                    onSelected("Location")
                }
            )
            AttachmentCell(
                name = "Audio",
                bgColor = AppColors.Error,
                icon = Icons.Default.RecordVoiceOver,
                onClick = {
                    Timber.d("[AttachmentBottomSheet] 选择 Audio")
                    onSelected("Audio")
                }
            )
            AttachmentCell(
                name = "Contact",
                bgColor = AppColors.OrangePrimary,
                icon = Icons.Default.Contacts,
                onClick = {
                    Timber.d("[AttachmentBottomSheet] 选择 Contact")
                    onSelected("Contact")
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/** 九宫格中的单个选项（圆形彩色背景 + 图标 + 下方文字
 *  定义为 RowScope 扩展函数，使得内部可以使用 Modifier.weight()
 */
@Composable
private fun RowScope.AttachmentCell(
    name: String,
    bgColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(bgColor)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            color = AppColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
