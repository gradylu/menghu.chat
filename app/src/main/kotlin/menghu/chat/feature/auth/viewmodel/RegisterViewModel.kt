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
import menghu.chat.core.utils.isValidNickname
import menghu.chat.feature.auth.repository.AuthRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 注册页面 ViewModel
 * 管理昵称、邮箱、密码、确认密码、加载态、错误信息、注册成功状态
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    fun updateName(value: String) {
        _name.value = value
        _errorMessage.value = null
    }

    fun updateEmail(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun updatePassword(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
        _errorMessage.value = null
    }

    /** 执行注册 */
    fun register() {
        val nameValue = _name.value.trim()
        val emailValue = _email.value.trim()
        val passwordValue = _password.value
        val confirmValue = _confirmPassword.value

        if (!nameValue.isValidNickname()) {
            _errorMessage.value = "请输入有效的昵称（1-20 个字符）"
            return
        }
        if (!emailValue.isValidEmail()) {
            _errorMessage.value = "请输入有效的邮箱地址"
            return
        }
        if (!passwordValue.isValidPassword()) {
            _errorMessage.value = "密码长度至少 6 位"
            return
        }
        if (passwordValue != confirmValue) {
            _errorMessage.value = "两次输入的密码不一致"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            Timber.d("[RegisterViewModel] 开始注册: %s", emailValue)

            val result = authRepository.register(nameValue, emailValue, passwordValue)
            _isLoading.value = false

            result.onSuccess {
                Timber.i("[RegisterViewModel] 注册成功")
                _registerSuccess.value = true
            }.onFailure {
                Timber.e(it, "[RegisterViewModel] 注册失败")
                _errorMessage.value = it.message ?: "注册失败，请稍后重试"
            }
        }
    }

    fun resetRegisterSuccess() {
        _registerSuccess.value = false
    }
}
