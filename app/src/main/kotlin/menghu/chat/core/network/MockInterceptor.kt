package menghu.chat.core.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber

/**
 * Mock 数据拦截器
 * 拦截所有网络请求，根据 URL 路径返回本地模拟 JSON 响应
 * 不发起真实 HTTP 请求，所有接口响应均在此处硬编码生成
 */
class MockInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        Timber.d("[MockInterceptor] 拦截请求: %s", url)

        // 根据路径返回对应 Mock JSON
        val mockResponse = when {
            url.contains("/auth/login") -> buildLoginMock()
            url.contains("/auth/register") -> buildRegisterMock()
            url.contains("/api/posts") -> buildPostsMock()
            url.contains("/api/messages") -> buildMessagesMock()
            url.contains("/api/stories") -> buildStoriesMock()
            url.contains("/api/friends") -> buildFriendsMock()
            url.contains("/api/nearby") -> buildNearbyMock()
            url.contains("/api/notifications") -> buildNotificationsMock()
            url.contains("/api/comments") -> buildCommentsMock()
            url.contains("/api/users") -> buildUsersMock()
            else -> buildEmptyMock()
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(mockResponse.toResponseBody("application/json".toMediaType()))
            .build()
    }

    /** 登录响应 Mock */
    private fun buildLoginMock(): String {
        return """
            {
                "success": true,
                "user": {
                    "id": 1,
                    "name": "梦狐用户",
                    "email": "menghu@chat.com",
                    "avatar": "https://example.com/avatar.png",
                    "bio": "这是一个梦狐社交的测试账号",
                    "occupation": "Android 工程师",
                    "postCount": 24,
                    "followerCount": 128,
                    "friendCount": 56,
                    "token": "mock_token_abc123"
                },
                "token": "mock_token_abc123",
                "message": "登录成功"
            }
        """.trimIndent()
    }

    /** 注册响应 Mock */
    private fun buildRegisterMock(): String {
        return """
            {
                "success": true,
                "user": {
                    "id": 2,
                    "name": "新用户",
                    "email": "newuser@chat.com",
                    "avatar": "https://example.com/newavatar.png",
                    "bio": "欢迎来到梦狐社交",
                    "occupation": "产品经理",
                    "postCount": 0,
                    "followerCount": 0,
                    "friendCount": 0,
                    "token": "mock_token_new456"
                },
                "token": "mock_token_new456",
                "message": "注册成功"
            }
        """.trimIndent()
    }

    /** 动态列表 Mock */
    private fun buildPostsMock(): String {
        return """
            {
                "success": true,
                "data": [
                    {
                        "id": 1,
                        "authorId": 2,
                        "authorName": "小明",
                        "authorAvatar": "https://example.com/xiaoming.png",
                        "content": "今天天气真好，出门散步！",
                        "images": "https://example.com/p1.jpg,https://example.com/p2.jpg",
                        "likeCount": 23,
                        "commentCount": 5,
                        "createdAt": ${System.currentTimeMillis() - 3600_000}
                    },
                    {
                        "id": 2,
                        "authorId": 3,
                        "authorName": "小红",
                        "authorAvatar": "https://example.com/xiaohong.png",
                        "content": "分享一段好看的代码～",
                        "images": "",
                        "likeCount": 12,
                        "commentCount": 3,
                        "createdAt": ${System.currentTimeMillis() - 7200_000}
                    }
                ]
            }
        """.trimIndent()
    }

    /** 消息列表 Mock */
    private fun buildMessagesMock(): String {
        return """
            {
                "success": true,
                "data": [
                    {
                        "id": 1,
                        "conversationId": 1,
                        "senderId": 0,
                        "type": "text",
                        "content": "你好！最近怎么样？",
                        "mediaUrl": "",
                        "isRead": true,
                        "status": "sent",
                        "createdAt": ${System.currentTimeMillis() - 600_000}
                    },
                    {
                        "id": 2,
                        "conversationId": 1,
                        "senderId": 2,
                        "type": "text",
                        "content": "挺好的，你呢？",
                        "mediaUrl": "",
                        "isRead": false,
                        "status": "sent",
                        "createdAt": ${System.currentTimeMillis() - 300_000}
                    }
                ]
            }
        """.trimIndent()
    }

    /** 故事 Mock */
    private fun buildStoriesMock(): String {
        return """
            {
                "success": true,
                "data": [
                    {
                        "id": 1,
                        "authorId": 2,
                        "authorName": "小明",
                        "authorAvatar": "https://example.com/xiaoming.png",
                        "mediaUrl": "https://example.com/story1.jpg",
                        "caption": "今天的夕阳真好看",
                        "expiresAt": ${System.currentTimeMillis() + 86400_000},
                        "createdAt": ${System.currentTimeMillis() - 3600_000}
                    }
                ]
            }
        """.trimIndent()
    }

    /** 好友列表 Mock */
    private fun buildFriendsMock(): String {
        return """
            {
                "success": true,
                "data": [
                    {
                        "id": 1,
                        "userId": 2,
                        "name": "小明",
                        "avatar": "https://example.com/xiaoming.png",
                        "status": "accepted",
                        "createdAt": ${System.currentTimeMillis() - 86400_000}
                    },
                    {
                        "id": 2,
                        "userId": 3,
                        "name": "小红",
                        "avatar": "https://example.com/xiaohong.png",
                        "status": "accepted",
                        "createdAt": ${System.currentTimeMillis() - 172800_000}
                    }
                ]
            }
        """.trimIndent()
    }

    /** 附近的人 Mock */
    private fun buildNearbyMock(): String {
        return """
            {
                "success": true,
                "data": [
                    {
                        "id": 1,
                        "userId": 101,
                        "name": "路人甲",
                        "avatar": "https://example.com/stranger1.png",
                        "distance": 120,
                        "signature": "热爱生活",
                        "createdAt": ${System.currentTimeMillis()}
                    },
                    {
                        "id": 2,
                        "userId": 102,
                        "name": "路人乙",
                        "avatar": "https://example.com/stranger2.png",
                        "distance": 350,
                        "signature": "喜欢旅行",
                        "createdAt": ${System.currentTimeMillis()}
                    }
                ]
            }
        """.trimIndent()
    }

    /** 通知列表 Mock */
    private fun buildNotificationsMock(): String {
        return """
            {
                "success": true,
                "data": [
                    {
                        "id": 1,
                        "type": "like",
                        "title": "新的点赞",
                        "content": "小明 点赞了你的动态",
                        "relatedUserId": 2,
                        "relatedUserName": "小明",
                        "relatedUserAvatar": "https://example.com/xiaoming.png",
                        "isRead": false,
                        "createdAt": ${System.currentTimeMillis() - 600_000}
                    }
                ]
            }
        """.trimIndent()
    }

    /** 评论列表 Mock */
    private fun buildCommentsMock(): String {
        return """
            {
                "success": true,
                "data": [
                    {
                        "id": 1,
                        "postId": 1,
                        "authorId": 3,
                        "authorName": "小红",
                        "authorAvatar": "https://example.com/xiaohong.png",
                        "content": "好漂亮！",
                        "createdAt": ${System.currentTimeMillis() - 1800_000}
                    }
                ]
            }
        """.trimIndent()
    }

    /** 用户列表 Mock */
    private fun buildUsersMock(): String {
        return """
            {
                "success": true,
                "data": [
                    {
                        "id": 1,
                        "name": "梦狐用户",
                        "email": "menghu@chat.com",
                        "avatar": "https://example.com/avatar.png",
                        "bio": "这是一个梦狐社交的测试账号",
                        "occupation": "Android 工程师",
                        "postCount": 24,
                        "followerCount": 128,
                        "friendCount": 56,
                        "token": "mock_token_abc123"
                    }
                ]
            }
        """.trimIndent()
    }

    /** 空响应 Mock（兜底） */
    private fun buildEmptyMock(): String {
        return """
            {
                "success": true,
                "message": "no content",
                "data": []
            }
        """.trimIndent()
    }
}
