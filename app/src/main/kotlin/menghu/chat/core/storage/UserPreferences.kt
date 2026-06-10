package menghu.chat.core.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * 扩展属性：全局单例 DataStore（应用级别）
 * 存储用户登录态、主题偏好、用户基本信息等轻量级配置
 */
private val Context.userDataStore by preferencesDataStore(
    name = "menghu_chat_user_prefs"
)

/**
 * 用户偏好配置管理
 * 基于 DataStore Preferences，提供异步、流式、类型安全的键值读写
 */
class UserPreferences(private val context: Context) {

    // 键定义
    private val keyToken = stringPreferencesKey("auth_token")
    private val keyUserId = longPreferencesKey("user_id")
    private val keyUserName = stringPreferencesKey("user_name")
    private val keyTheme = stringPreferencesKey("app_theme")
    private val keyIsLoggedIn = booleanPreferencesKey("is_logged_in")

    /** 是否已登录 Flow */
    val isLoggedInFlow: Flow<Boolean> = context.userDataStore.data
        .map { preferences ->
            preferences[keyIsLoggedIn] ?: false
        }

    /** 当前登录用户 ID Flow */
    val userIdFlow: Flow<Long> = context.userDataStore.data
        .map { preferences ->
            preferences[keyUserId] ?: 0L
        }

    /** 登录 Token Flow */
    val tokenFlow: Flow<String> = context.userDataStore.data
        .map { preferences ->
            preferences[keyToken].orEmpty()
        }

    /** 用户名 Flow */
    val userNameFlow: Flow<String> = context.userDataStore.data
        .map { preferences ->
            preferences[keyUserName].orEmpty()
        }

    /** 主题偏好 Flow */
    val themeFlow: Flow<String> = context.userDataStore.data
        .map { preferences ->
            preferences[keyTheme] ?: "light"
        }

    /** 保存登录信息 */
    suspend fun saveLoginInfo(token: String, userId: Long, userName: String) {
        context.userDataStore.edit { preferences ->
            preferences[keyToken] = token
            preferences[keyUserId] = userId
            preferences[keyUserName] = userName
            preferences[keyIsLoggedIn] = true
        }
    }

    /** 清除登录信息（登出） */
    suspend fun clearLoginInfo() {
        context.userDataStore.edit { preferences ->
            preferences.remove(keyToken)
            preferences.remove(keyUserId)
            preferences.remove(keyUserName)
            preferences[keyIsLoggedIn] = false
        }
    }

    /** 保存主题偏好 */
    suspend fun saveTheme(theme: String) {
        context.userDataStore.edit { preferences ->
            preferences[keyTheme] = theme
        }
    }

    /** 一次性读取是否已登录（使用 first() 读取当前值，避免无限流阻塞） */
    suspend fun isLoggedIn(): Boolean {
        return try {
            context.userDataStore.data.first()[keyIsLoggedIn] ?: false
        } catch (ex: Exception) {
            Timber.e(ex, "[UserPreferences] isLoggedIn 读取异常")
            false
        }
    }
}
