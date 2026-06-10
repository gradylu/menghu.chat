package menghu.chat.feature.auth.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import menghu.chat.core.ui.theme.MenghuChatTheme
import menghu.chat.feature.auth.viewmodel.StartupViewModel
import menghu.chat.navigation.Destinations
import menghu.chat.navigation.MenghuNavHost
import timber.log.Timber

/**
 * 应用唯一 Activity
 * - 通过 Hilt 注入依赖
 * - setContent 中启动时异步读取登录状态，根据 isLoggedIn 决定起始路由
 * - 未登录 → 登录页；已登录 → 直接进入首页 Home
 */
@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("[LoginActivity] onCreate：应用启动")

        setContent {
            MenghuChatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator()
                }
            }
        }
    }
}

/**
 * 启动页导航容器
 * - 启动时异步检查 DataStore 中的登录态
 * - 检查完成前显示加载圈
 * - 已登录 → 直接进入 Home；未登录 → 进入 Login
 */
@Composable
private fun AppNavigator(
    startupViewModel: StartupViewModel = hiltViewModel()
) {
    // 从 StartupViewModel 读取登录态：null=加载中，true=已登录，false=未登录
    val isLoggedInState: State<Boolean?> = startupViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isLoggedIn: Boolean? = isLoggedInState.value

    // 启动时执行一次检查（由 ViewModel 自身完成）
    LaunchedEffect(Unit) {
        Timber.d("[AppNavigator] 正在检查登录状态...")
    }

    // 检查中 → 显示加载圈
    if (isLoggedIn == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // 检查完成 → 根据登录态决定起始路由
        val startDest = if (isLoggedIn) Destinations.Home.route else Destinations.Login.route
        Timber.d("[AppNavigator] 起始路由: %s (isLoggedIn=%s)", startDest, isLoggedIn)

        val navController = rememberNavController()
        MenghuNavHost(
            navController = navController,
            startDestination = startDest
        )
    }
}
