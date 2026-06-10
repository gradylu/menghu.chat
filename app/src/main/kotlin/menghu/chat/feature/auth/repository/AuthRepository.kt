package menghu.chat.feature.auth.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import menghu.chat.feature.auth.datasource.AuthLocalDataSource
import menghu.chat.feature.auth.datasource.AuthRemoteDataSource
import menghu.chat.feature.auth.model.toEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 鉴权仓库层
 * 聚合网络与本地数据源，提供登录、注册、登录态、登出能力
 * 唯一数据入口，ViewModel 不直接访问网络/数据库
 */
@Singleton
class AuthRepository @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource
) {

    /** 登录：先调网络接口，成功后保存用户信息与登录态到本地 */
    suspend fun login(email: String, password: String): Result<Boolean> {
        Timber.d("[AuthRepository] 登录: email=%s", email)
        return try {
            val response = remoteDataSource.login(email, password)
            if (response.success && response.user != null) {
                // 保存用户到 Room
                val userEntity = response.user.toEntity()
                localDataSource.saveUser(userEntity)
                // 保存 token/ID 到 DataStore
                localDataSource.saveLoginInfo(
                    token = response.token.orEmpty(),
                    userId = response.user.id,
                    userName = response.user.name
                )
                Timber.i("[AuthRepository] 登录成功")
                Result.success(true)
            } else {
                Timber.w("[AuthRepository] 登录失败: %s", response.message)
                Result.failure(Exception(response.message ?: "登录失败"))
            }
        } catch (e: Exception) {
            Timber.e(e, "[AuthRepository] 登录异常")
            Result.failure(e)
        }
    }

    /** 注册：类似登录流程 */
    suspend fun register(name: String, email: String, password: String): Result<Boolean> {
        Timber.d("[AuthRepository] 注册: email=%s", email)
        return try {
            val response = remoteDataSource.register(name, email, password)
            if (response.success && response.user != null) {
                val userEntity = response.user.toEntity()
                localDataSource.saveUser(userEntity)
                localDataSource.saveLoginInfo(
                    token = response.token.orEmpty(),
                    userId = response.user.id,
                    userName = response.user.name
                )
                Timber.i("[AuthRepository] 注册成功")
                Result.success(true)
            } else {
                Timber.w("[AuthRepository] 注册失败: %s", response.message)
                Result.failure(Exception(response.message ?: "注册失败"))
            }
        } catch (e: Exception) {
            Timber.e(e, "[AuthRepository] 注册异常")
            Result.failure(e)
        }
    }

    /** 是否已经登录 */
    suspend fun isLoggedIn(): Boolean = localDataSource.isLoggedIn()

    /** 观察登录态 Flow */
    fun observeLoginState(): Flow<Boolean> = localDataSource.observeLoginState()

    /** 登出 */
    suspend fun logout() {
        Timber.i("[AuthRepository] 登出")
        localDataSource.clearLoginInfo()
    }
}
