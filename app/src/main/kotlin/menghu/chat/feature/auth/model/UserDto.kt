package menghu.chat.feature.auth.model

/**
 * 用户 DTO（网络传输层模型）
 * 与 Mock 接口返回的 JSON 结构对应
 */
data class UserDto(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val avatar: String = "",
    val bio: String = "",
    val occupation: String = "",
    val postCount: Int = 0,
    val followerCount: Int = 0,
    val friendCount: Int = 0,
    val token: String = ""
)

/**
 * 用户领域模型（业务逻辑层使用）
 * 不耦合任何数据库或网络字段
 */
data class UserDomain(
    val id: Long,
    val name: String,
    val email: String,
    val avatar: String,
    val bio: String,
    val occupation: String,
    val postCount: Int,
    val followerCount: Int,
    val friendCount: Int,
    val token: String
)

/**
 * 登录请求参数
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * 登录响应
 */
data class LoginResponse(
    val success: Boolean,
    val user: UserDto?,
    val token: String?,
    val message: String?
)

/**
 * 注册请求参数
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

/**
 * 注册响应
 */
data class RegisterResponse(
    val success: Boolean,
    val user: UserDto?,
    val token: String?,
    val message: String?
)
