package menghu.chat.core.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.navigation.Destinations

/**
 * 底部导航栏
 * 包含 4 个 Tab：Feed / Messages / Friends / Profile
 * - 选中态：橙色图标 + 橙色文字
 * - 未选中态：灰色图标 + 灰色文字
 *
 * @param currentRoute 当前路由，用于高亮选中的 Tab
 * @param onNavigate 路由变更回调
 */
@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    // 底部导航 Tab 项定义
    val items = listOf(
        BottomNavItem(
            label = "Feed",
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Feed",
                    modifier = Modifier.size(22.dp)
                )
            },
            route = Destinations.Feed.route
        ),
        BottomNavItem(
            label = "Messages",
            icon = {
                Icon(
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = "Messages",
                    modifier = Modifier.size(22.dp)
                )
            },
            route = Destinations.Messages.route
        ),
        BottomNavItem(
            label = "Friends",
            icon = {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "Friends",
                    modifier = Modifier.size(22.dp)
                )
            },
            route = Destinations.Friends.route
        ),
        BottomNavItem(
            label = "Profile",
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(22.dp)
                )
            },
            route = Destinations.Profile.route
        )
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = AppColors.OrangePrimary,
        tonalElevation = 4.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = item.icon,
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppColors.OrangePrimary,
                    selectedTextColor = AppColors.OrangePrimary,
                    unselectedIconColor = AppColors.TextSecondary,
                    unselectedTextColor = AppColors.TextSecondary,
                    indicatorColor = AppColors.OrangeLight.copy(alpha = 0.25f)
                )
            )
        }
    }
}

/**
 * 底部导航项数据结构
 */
private data class BottomNavItem(
    val label: String,
    val icon: @Composable () -> Unit,
    val route: String
)
