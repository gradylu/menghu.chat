package menghu.chat.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import menghu.chat.feature.auth.repository.AuthRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 应用启动 ViewModel
 * - 负责启动时异步读取 DataStore 中的登录态
 * - 暴露 `isLoggedIn: StateFlow<Boolean?>`：null = 加载中，true = 已登录，false = 未登录
 * - 供 LoginActivity 中的 AppNavigator 根据此值决定起始路由
 */
@HiltViewModel
class StartupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 登录态：null=加载中，true=已登录，false=未登录
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    init {
        checkLoginState()
    }

    /**
     * 异步读取 DataStore 中的登录状态
     */
    private fun checkLoginState() {
        viewModelScope.launch {
            try {
                val loggedIn = authRepository.isLoggedIn()
                Timber.d("[StartupViewModel] 登录态检查完成: isLoggedIn=%s", loggedIn)
                _isLoggedIn.value = loggedIn
            } catch (ex: Exception) {
                Timber.e(ex, "[StartupViewModel] 登录态检查异常")
                _isLoggedIn.value = false
            }
        }
    }
}
