package menghu.chat.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import menghu.chat.core.ui.component.BottomNavBar
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.feature.chat.ui.MessagesScreen
import menghu.chat.feature.moment.ui.FeedScreen
import menghu.chat.navigation.Destinations

/**
 * 首页 Screen
 * - 仅负责 Tab 容器：底部 BottomNavBar + 内部嵌套 NavHost
 * - ✅ 各 Tab 页面（Feed / Messages / Friends / Profile）各自管理自己的头部（topBar）
 * - ❌ 此处不再设置外层 Scaffold topBar，避免与子页面的 topBar 重复显示
 *
 * @param rootNavController 上层根 NavController（用于跳出 Home 去往其他页面）
 */
@Composable
fun HomeScreen(
    rootNavController: androidx.navigation.NavHostController? = null
) {
    // 内部 Tab NavController
    val tabNavController = rememberNavController()

    // 读取当前 Tab 路由，用于底部导航的选中态
    val backStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Destinations.Feed.route

    Scaffold(
        containerColor = AppColors.Background,
        // ⚠️ 注意：外层不设置 topBar，由各 Tab 页面（FeedScreen / MessagesScreen /
        // FriendsScreen / ProfileScreen）内部的 Scaffold 自行管理顶部栏。
        // 这样每个 Tab 可以有自己专属的头部设计（如 Messages 的搜索框、Friends 的标题栏等）。
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    tabNavController.navigate(route) {
                        // 避免多重返回栈：回到 startDestination
                        popUpTo(tabNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        // 内部 Tab 嵌套导航图
        NavHost(
            navController = tabNavController,
            startDestination = Destinations.Feed.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Feed Tab 使用实际动态流页面，跨页跳转采用根 NavController
            composable(Destinations.Feed.route) {
                if (rootNavController != null) {
                    FeedScreen(navController = rootNavController)
                } else {
                    PlaceholderScreen(title = "Feed")
                }
            }
            composable(Destinations.Messages.route) {
                if (rootNavController != null) {
                    MessagesScreen(navController = rootNavController)
                } else {
                    PlaceholderScreen(title = "Messages")
                }
            }
            composable(Destinations.Friends.route) {
                if (rootNavController != null) {
                    menghu.chat.feature.friend.ui.FriendsScreen(navController = rootNavController)
                } else {
                    PlaceholderScreen(title = "Friends")
                }
            }
            composable(Destinations.Profile.route) {
                if (rootNavController != null) {
                    menghu.chat.feature.profile.ui.ProfileScreen(
                        onNotificationClick = { rootNavController.navigate(menghu.chat.navigation.Destinations.Notification.route) }
                    )
                } else {
                    PlaceholderScreen(title = "Profile")
                }
            }
        }
    }
}

/**
 * 占位 Screen：简单在屏幕中心显示一个标题文字
 */
@Composable
internal fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = AppColors.OrangePrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
