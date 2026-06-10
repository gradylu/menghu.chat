package menghu.chat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import menghu.chat.feature.auth.ui.LoginScreen
import menghu.chat.feature.auth.ui.SignUpScreen
import menghu.chat.feature.chat.ui.ChatScreen
import menghu.chat.feature.chat.ui.MessagesScreen
import menghu.chat.feature.chat.ui.VideoCallScreen
import menghu.chat.feature.friend.ui.FollowerProfileScreen
import menghu.chat.feature.friend.ui.FriendsScreen
import menghu.chat.feature.friend.ui.NearbyListScreen
import menghu.chat.feature.friend.ui.NearbyRadarScreen
import menghu.chat.feature.friend.ui.SendRequestScreen
import menghu.chat.feature.home.ui.HomeScreen
import menghu.chat.feature.home.ui.PlaceholderScreen
import menghu.chat.feature.moment.ui.CommentsScreen
import menghu.chat.feature.moment.ui.FeedScreen
import menghu.chat.feature.moment.ui.NewPostImageScreen
import menghu.chat.feature.moment.ui.NewPostTextScreen
import menghu.chat.feature.moment.ui.StoryScreen
import menghu.chat.feature.notification.ui.NotificationScreen
import menghu.chat.feature.profile.ui.ProfileScreen
import menghu.chat.feature.settings.ui.SettingsScreen
import timber.log.Timber

/**
 * 梦狐社交全局 NavHost
 * - 起始路由：由调用方决定（登录态检测后传入）
 * - 登录 / 注册 → 首页（HomeScreen 内部管理四个 Tab，其中 Feed 为动态流
 * - Feed 内可以跳转到 Story、NewPost、NewPostText、Comments、Profile、Notification、Settings 等
 *
 * @param navController 外部传入的 NavHostController
 * @param startDestination 起始路由：已登录 → Home，未登录 → Login
 */
@Composable
fun MenghuNavHost(
    navController: NavHostController,
    startDestination: String = Destinations.Login.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // ===== 登录 / 注册 =====
        composable(Destinations.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Destinations.Register.route) }
            )
        }

        composable(Destinations.Register.route) {
            SignUpScreen(
                onRegisterSuccess = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // ===== 首页（内部通过 HomeScreen 自行管理四个 Tab =====
        composable(Destinations.Home.route) {
            HomeScreen(rootNavController = navController)
        }

        // ===== 动态流（Feed）=====
        composable(Destinations.Feed.route) {
            Timber.d("[NavHost] 进入 Feed 动态流")
            FeedScreen(navController = navController)
        }

        // ===== 故事（Story）全屏页 =====
        composable(Destinations.Story.route) {
            Timber.d("[NavHost] 进入 Story 全屏页")
            StoryScreen(navController = navController)
        }

        // ===== 发布第 1 步：图片选择 + 滤镜 =====
        composable(Destinations.NewPost.route) {
            Timber.d("[NavHost] 进入 NewPost 图片选择页")
            NewPostImageScreen(navController = navController)
        }

        // ===== 发布第 2 步：文字描述 + 发布 =====
        composable(
            route = "${Destinations.NewPostText.route}?imageUrl={imageUrl}",
            arguments = listOf(
                navArgument("imageUrl") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { entry ->
            val imageUrl = entry.arguments?.getString("imageUrl")
            Timber.d("[NavHost] 进入 NewPostText，携带 imageUrl=%s", imageUrl)
            NewPostTextScreen(
                navController = navController,
                imageUrl = imageUrl
            )
        }

        // ===== 评论页（postId 必传）=====
        composable(
            route = Destinations.Comments.route,
            arguments = listOf(
                navArgument("postId") { type = NavType.LongType }
            )
        ) { entry ->
            val postId = entry.arguments?.getLong("postId") ?: 0L
            Timber.d("[NavHost] 进入评论页，postId=%d", postId)
            CommentsScreen(
                navController = navController,
                postId = postId
            )
        }

        // ===== 消息与会话 =====
        composable(Destinations.Messages.route) {
            Timber.d("[NavHost] 进入 Messages 会话列表")
            MessagesScreen(navController = navController)
        }
        composable(
            route = Destinations.Chat.route,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.LongType }
            )
        ) { entry ->
            val conversationId = entry.arguments?.getLong("conversationId") ?: 0L
            Timber.d("[NavHost] 进入 Chat 详情，conversationId=%d", conversationId)
            // 显式将路由参数传入 ChatScreen，避免 ViewModel 仅依赖 SavedStateHandle 时的类型歧义
            ChatScreen(
                navController = navController,
                conversationId = conversationId
            )
        }
        composable(Destinations.VideoCall.route) {
            Timber.d("[NavHost] 进入 VideoCall 占位页")
            VideoCallScreen(navController = navController)
        }

        // ===== 好友 / 附近 =====
        composable(Destinations.Friends.route) {
            Timber.d("[NavHost] 进入好友列表 FriendsScreen")
            FriendsScreen(navController = navController)
        }
        composable(Destinations.Nearby.route) {
            Timber.d("[NavHost] 进入附近雷达页")
            NearbyRadarScreen(navController = navController)
        }
        composable(Destinations.NearbyList.route) {
            Timber.d("[NavHost] 进入附近用户列表")
            NearbyListScreen(navController = navController)
        }
        composable(
            route = Destinations.SendRequest.route,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { entry ->
            val userId = entry.arguments?.getLong("userId") ?: 0L
            Timber.d("[NavHost] 进入发送好友请求，userId=%d", userId)
            SendRequestScreen(navController = navController, userId = userId)
        }
        composable(
            route = Destinations.FollowerProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { entry ->
            val userId = entry.arguments?.getLong("userId") ?: 0L
            Timber.d("[NavHost] 进入粉丝资料页，userId=%d", userId)
            FollowerProfileScreen(navController = navController, userId = userId)
        }

        // ===== 个人主页 =====
        composable(Destinations.Profile.route) {
            Timber.d("[NavHost] 进入 Profile 个人主页")
            ProfileScreen(
                onNotificationClick = { navController.navigate(Destinations.Notification.route) }
            )
        }

        // ===== 通知中心 =====
        composable(Destinations.Notification.route) {
            Timber.d("[NavHost] 进入 Notification 通知中心")
            NotificationScreen()
        }

        // ===== 设置页 =====
        composable(Destinations.Settings.route) {
            Timber.d("[NavHost] 进入 Settings 设置页")
            SettingsScreen(
                onLogoutSuccess = {
                    // 登出成功 → 回到登录页
                    navController.navigate(Destinations.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
