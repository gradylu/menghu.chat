package menghu.chat.feature.settings.viewmodel

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
 * 设置页 ViewModel
 * - 暴露 logout() 方法：调用仓库层清除登录态，返回 Flow<Boolean>
 *
 * @param authRepository 鉴权/用户仓库
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 登出结果（供 UI 监听并跳转
    private val _logoutResult = MutableStateFlow(false)
    val logoutResult: StateFlow<Boolean> = _logoutResult.asStateFlow()

    /**
     * 登出：调用仓库层清除登录信息
     */
    fun logout() {
        viewModelScope.launch {
            Timber.d("[SettingsViewModel] 开始登出")
            authRepository.logout()
            _logoutResult.value = true
            Timber.i("[SettingsViewModel] 登出完成")
        }
    }
}
