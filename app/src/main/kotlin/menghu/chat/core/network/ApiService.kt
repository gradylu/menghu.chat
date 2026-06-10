package menghu.chat.core.network

import menghu.chat.feature.auth.model.LoginRequest
import menghu.chat.feature.auth.model.LoginResponse
import menghu.chat.feature.auth.model.RegisterRequest
import menghu.chat.feature.auth.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 网络服务接口定义
 * 所有请求均被 MockInterceptor 拦截，返回本地模拟 JSON 数据
 * 此处定义仅作为接口签名，无实际网络调用
 */
interface ApiService {

    /** 登录接口 */
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    /** 注册接口 */
    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    /** 获取动态列表 */
    @GET("/api/posts")
    suspend fun fetchPosts(@Query("page") page: Int = 1, @Query("size") size: Int = 20): retrofit2.Response<Any>

    /** 获取消息列表 */
    @GET("/api/messages")
    suspend fun fetchMessages(@Query("conversationId") conversationId: Long): retrofit2.Response<Any>

    /** 获取故事列表 */
    @GET("/api/stories")
    suspend fun fetchStories(): retrofit2.Response<Any>

    /** 获取好友列表 */
    @GET("/api/friends")
    suspend fun fetchFriends(): retrofit2.Response<Any>

    /** 获取附近的人 */
    @GET("/api/nearby")
    suspend fun fetchNearbyUsers(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): retrofit2.Response<Any>

    /** 获取通知列表 */
    @GET("/api/notifications")
    suspend fun fetchNotifications(): retrofit2.Response<Any>

    /** 获取评论列表 */
    @GET("/api/comments")
    suspend fun fetchComments(@Query("postId") postId: Long): retrofit2.Response<Any>

    /** 获取用户列表 */
    @GET("/api/users")
    suspend fun fetchUsers(): retrofit2.Response<Any>
}
