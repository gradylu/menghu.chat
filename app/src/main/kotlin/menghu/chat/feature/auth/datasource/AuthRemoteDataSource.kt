package menghu.chat.feature.auth.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import menghu.chat.core.network.ApiService
import menghu.chat.feature.auth.model.LoginRequest
import menghu.chat.feature.auth.model.LoginResponse
import menghu.chat.feature.auth.model.RegisterRequest
import menghu.chat.feature.auth.model.RegisterResponse
import menghu.chat.feature.auth.model.UserDto
import timber.log.Timber
import javax.inject.Inject

/**
 * 鉴权网络数据源
 * 封装登录、注册等接口调用
 * 底层走 MockInterceptor，实际不发起真实网络请求
 */
class AuthRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {

    /** 登录接口 */
    suspend fun login(email: String, password: String): LoginResponse {
        Timber.d("[AuthRemoteDataSource] 调用登录接口: email=%s", email)
        val request = LoginRequest(email = email, password = password)
        return apiService.login(request)
    }

    /** 注册接口 */
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        Timber.d("[AuthRemoteDataSource] 调用注册接口: email=%s", email)
        val request = RegisterRequest(name = name, email = email, password = password)
        return apiService.register(request)
    }

    /** 获取用户列表（Flow 形式） */
    fun fetchUsers(): Flow<List<UserDto>> = flow {
        try {
            val response = apiService.fetchUsers()
            // Mock 场景下响应体为泛型 Any，此处简化直接返回空列表
            Timber.d("[AuthRemoteDataSource] fetchUsers 执行成功")
            emit(emptyList())
        } catch (e: Exception) {
            Timber.e(e, "[AuthRemoteDataSource] fetchUsers 失败")
            emit(emptyList())
        }
    }
}
