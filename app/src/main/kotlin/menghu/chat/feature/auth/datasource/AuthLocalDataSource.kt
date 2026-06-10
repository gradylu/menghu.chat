package menghu.chat.feature.auth.datasource

import kotlinx.coroutines.flow.Flow
import menghu.chat.core.storage.UserPreferences
import menghu.chat.feature.auth.model.UserDao
import menghu.chat.feature.auth.model.UserEntity
import timber.log.Timber
import javax.inject.Inject

/**
 * 鉴权本地数据源
 * 封装 Room 数据库用户表读写、DataStore 登录态维护
 */
class AuthLocalDataSource @Inject constructor(
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) {

    /** 保存/更新用户信息 */
    suspend fun saveUser(user: UserEntity) {
        Timber.d("[AuthLocalDataSource] 保存用户: id=%s, name=%s", user.id, user.name)
        userDao.insertUser(user)
    }

    /** 根据 ID 查询用户 */
    suspend fun getUserById(id: Long): UserEntity? {
        return userDao.getUserById(id)
    }

    /** 获取当前登录态 Flow */
    fun observeLoginState(): Flow<Boolean> = userPreferences.isLoggedInFlow

    /** 保存登录信息到 DataStore */
    suspend fun saveLoginInfo(token: String, userId: Long, userName: String) {
        Timber.d("[AuthLocalDataSource] 保存登录信息: userId=%s", userId)
        userPreferences.saveLoginInfo(token, userId, userName)
    }

    /** 清除登录信息 */
    suspend fun clearLoginInfo() {
        Timber.d("[AuthLocalDataSource] 清除登录信息")
        userPreferences.clearLoginInfo()
    }

    /** 是否已经登录 */
    suspend fun isLoggedIn(): Boolean = userPreferences.isLoggedIn()
}
