package menghu.chat.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import menghu.chat.core.utils.isValidEmail
import menghu.chat.core.utils.isValidPassword
import menghu.chat.feature.auth.repository.AuthRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 登录页面 ViewModel
 * 管理邮箱、密码、加载态、错误信息、登录成功状态
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 邮箱
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    // 密码
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // 加载态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误信息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 登录成功
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    /** 更新邮箱 */
    fun updateEmail(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    /** 更新密码 */
    fun updatePassword(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    /** 执行登录 */
    fun login() {
        // 表单校验
        val emailValue = _email.value.trim()
        val passwordValue = _password.value

        if (!emailValue.isValidEmail()) {
            _errorMessage.value = "请输入有效的邮箱地址"
            return
        }
        if (!passwordValue.isValidPassword()) {
            _errorMessage.value = "密码长度至少 6 位"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            Timber.d("[LoginViewModel] 开始登录: %s", emailValue)

            val result = authRepository.login(emailValue, passwordValue)
            _isLoading.value = false

            result.onSuccess {
                Timber.i("[LoginViewModel] 登录成功")
                _loginSuccess.value = true
            }.onFailure {
                Timber.e(it, "[LoginViewModel] 登录失败")
                _errorMessage.value = it.message ?: "登录失败，请稍后重试"
            }
        }
    }

    /** 重置登录成功标记 */
    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }
}
