package menghu.chat.navigation

/**
 * 应用内所有页面路由定义
 * 所有 Compose Screen 路由集中管理，避免硬编码路径字符串
 */
sealed class Destinations(val route: String) {

    /** 登录页 */
    object Login : Destinations("login")

    /** 注册页 */
    object Register : Destinations("register")

    /** 首页（动态 Feed） */
    object Home : Destinations("home")

    /** 动态列表页（Feed 流） */
    object Feed : Destinations("feed")

    /** 消息列表页 */
    object Messages : Destinations("messages")

    /** 好友列表页 */
    object Friends : Destinations("friends")

    /** 个人中心 */
    object Profile : Destinations("profile")

    /** 故事页（全屏大图） */
    object Story : Destinations("story")

    /** 发布新动态第 1 步（图片选择 + 滤镜） */
    object NewPost : Destinations("new_post")

    /** 发布新动态第 2 步（文字描述 + 发布），可选携带图片 URL */
    object NewPostText : Destinations("new_post_text")

    /** 评论页（携带 postId 参数） */
    object Comments : Destinations("comments/{postId}") {
        fun withPostId(postId: Long) = "comments/$postId"
    }

    /** 聊天详情页 */
    object Chat : Destinations("chat/{conversationId}") {
        fun withConversationId(conversationId: Long) = "chat/$conversationId"
    }

    /** 附近的人 */
    object Nearby : Destinations("nearby")

    /** 附近用户列表 */
    object NearbyList : Destinations("nearby_list")

    /** 发送好友申请 */
    object SendRequest : Destinations("send_request/{userId}") {
        fun withUserId(userId: Long) = "send_request/$userId"
    }

    /** 粉丝/关注者资料 */
    object FollowerProfile : Destinations("follower_profile/{userId}") {
        fun withUserId(userId: Long) = "follower_profile/$userId"
    }

    /** 通知中心 */
    object Notification : Destinations("notification")

    /** 设置页 */
    object Settings : Destinations("settings")

    /** 视频通话页 */
    object VideoCall : Destinations("video_call/{userId}") {
        fun withUserId(userId: Long) = "video_call/$userId"
    }
}
