package menghu.chat.feature.moment.repository

import kotlinx.coroutines.flow.Flow
import menghu.chat.feature.moment.datasource.MomentLocalDataSource
import menghu.chat.feature.moment.model.CommentEntity
import menghu.chat.feature.moment.model.PostEntity
import menghu.chat.feature.moment.model.StoryEntity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 动态（帖子）仓库层
 * 聚合 Room 本地数据源，对外暴露动态、故事、评论的流式与一次性读写能力
 * 负责构造 mock 样例数据，保证首次进入时能直接看到完整的 Feed 效果
 */
@Singleton
class MomentRepository @Inject constructor(
    private val localDataSource: MomentLocalDataSource
) {

    // ======================= 动态（Post） =======================

    /** 流式获取动态列表 */
    fun observePosts(): Flow<List<PostEntity>> = localDataSource.observePosts()

    /** 一次性获取动态列表 */
    suspend fun getAllPosts(): List<PostEntity> = localDataSource.getAllPosts()

    /** 发布一条新动态（支持文本 + 图片列表） */
    suspend fun publishPost(
        content: String,
        images: List<String> = emptyList(),
        authorId: Long = 1L,
        authorName: String = "我",
        authorAvatar: String = ""
    ) {
        Timber.d("[MomentRepository] 发布新动态: content=%s, images=%d", content.take(20), images.size)
        val post = PostEntity(
            id = 0, // Room 自动分配
            authorId = authorId,
            authorName = authorName,
            authorAvatar = authorAvatar,
            content = content,
            images = images.joinToString(","),
            likeCount = 0,
            commentCount = 0,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.savePost(post)
    }

    /** 删除一条动态 */
    suspend fun deletePost(id: Long) = localDataSource.deletePost(id)

    // ======================= 故事（Story） =======================

    /** 流式获取故事列表 */
    fun observeStories(): Flow<List<StoryEntity>> = localDataSource.observeStories()

    /** 一次性获取故事列表 */
    suspend fun getAllStories(): List<StoryEntity> = localDataSource.getAllStories()

    // ======================= 评论（Comment） =======================

    /** 流式获取某条动态下的评论列表 */
    fun observeComments(postId: Long): Flow<List<CommentEntity>> =
        localDataSource.observeComments(postId)

    /** 发送一条评论 */
    suspend fun sendComment(
        postId: Long,
        content: String,
        authorName: String = "我",
        authorAvatar: String = ""
    ) {
        Timber.d("[MomentRepository] 发送评论: postId=%d, content=%s", postId, content.take(20))
        val comment = CommentEntity(
            id = 0,
            postId = postId,
            authorId = 1L,
            authorName = authorName,
            authorAvatar = authorAvatar,
            content = content,
            commentCount = 0,
            likeCount = 0,
            createdAt = System.currentTimeMillis()
        )
        localDataSource.saveComment(comment)
    }

    // ======================= Mock 样例数据 =======================

    /**
     * 首次进入时插入一批 Mock 动态、故事、评论样例
     * 若数据库中已存在动态则跳过，避免重复写入
     */
    suspend fun insertSampleDataIfEmpty() {
        val existingPosts = localDataSource.getAllPosts()
        if (existingPosts.isNotEmpty()) {
            Timber.i("[MomentRepository] 数据库已有动态，跳过 Mock 数据插入")
            return
        }

        // 构造 6 条动态（文本 + 多图 + 点赞/评论数）
        val samplePosts = listOf(
            PostEntity(
                id = 0,
                authorId = 101L,
                authorName = "Hossein",
                authorAvatar = "https://picsum.photos/seed/hossein/160/160",
                content = "Look my collection, i really want to share about my last trip to Bali. Please check guys!",
                images = listOf(
                    "https://picsum.photos/seed/bali1/600/600",
                    "https://picsum.photos/seed/bali2/600/600",
                    "https://picsum.photos/seed/bali3/600/600"
                ).joinToString(","),
                likeCount = 24000,
                commentCount = 1200,
                createdAt = System.currentTimeMillis() - 120_000L // 2 分钟前
            ),
            PostEntity(
                id = 0,
                authorId = 102L,
                authorName = "Ralph",
                authorAvatar = "https://picsum.photos/seed/ralph/160/160",
                content = "今天的城市夜景太美了，随手拍了几张。",
                images = listOf(
                    "https://picsum.photos/seed/citynight1/600/600",
                    "https://picsum.photos/seed/citynight2/600/600"
                ).joinToString(","),
                likeCount = 50230,
                commentCount = 3100,
                createdAt = System.currentTimeMillis() - 3_600_000L // 1 小时前
            ),
            PostEntity(
                id = 0,
                authorId = 103L,
                authorName = "Theresa",
                authorAvatar = "https://picsum.photos/seed/theresa/160/160",
                content = "新入的咖啡豆，第一次尝试手冲，大家有推荐的参数吗？",
                images = listOf(
                    "https://picsum.photos/seed/coffee/600/600"
                ).joinToString(","),
                likeCount = 8421,
                commentCount = 312,
                createdAt = System.currentTimeMillis() - 7_200_000L // 2 小时前
            ),
            PostEntity(
                id = 0,
                authorId = 104L,
                authorName = "Wendy",
                authorAvatar = "https://picsum.photos/seed/wendy/160/160",
                content = "周末去了一趟郊外的樱花林，春天真的来了。",
                images = listOf(
                    "https://picsum.photos/seed/sakura1/600/600",
                    "https://picsum.photos/seed/sakura2/600/600",
                    "https://picsum.photos/seed/sakura3/600/600"
                ).joinToString(","),
                likeCount = 15220,
                commentCount = 620,
                createdAt = System.currentTimeMillis() - 86_400_000L // 1 天前
            ),
            PostEntity(
                id = 0,
                authorId = 105L,
                authorName = "Kevin",
                authorAvatar = "https://picsum.photos/seed/kevin/160/160",
                content = "最近在读《人月神话》，重新体会一下软件工程中的那些经典洞见。",
                images = "",
                likeCount = 2210,
                commentCount = 112,
                createdAt = System.currentTimeMillis() - 172_800_000L // 2 天前
            ),
            PostEntity(
                id = 0,
                authorId = 106L,
                authorName = "Julia",
                authorAvatar = "https://picsum.photos/seed/julia/160/160",
                content = "今天尝试了新的烘焙配方，口感出乎意料地松软 🥐",
                images = listOf(
                    "https://picsum.photos/seed/croissant1/600/600",
                    "https://picsum.photos/seed/croissant2/600/600"
                ).joinToString(","),
                likeCount = 9300,
                commentCount = 420,
                createdAt = System.currentTimeMillis() - 259_200_000L // 3 天前
            )
        )

        // 构造 4 条故事（顶部横向滚动区域展示）
        val sampleStories = listOf(
            StoryEntity(
                id = 0,
                authorId = 101L,
                authorName = "Hossein",
                authorAvatar = "https://picsum.photos/seed/hossein/160/160",
                mediaUrl = "https://picsum.photos/seed/story1/900/1400",
                caption = "Bali sunset 🌅",
                expiresAt = System.currentTimeMillis() + 24 * 3_600_000L,
                createdAt = System.currentTimeMillis() - 60 * 60_000L
            ),
            StoryEntity(
                id = 0,
                authorId = 102L,
                authorName = "Ralph",
                authorAvatar = "https://picsum.photos/seed/ralph/160/160",
                mediaUrl = "https://picsum.photos/seed/story2/900/1400",
                caption = "City lights ✨",
                expiresAt = System.currentTimeMillis() + 24 * 3_600_000L,
                createdAt = System.currentTimeMillis() - 90 * 60_000L
            ),
            StoryEntity(
                id = 0,
                authorId = 103L,
                authorName = "Theresa",
                authorAvatar = "https://picsum.photos/seed/theresa/160/160",
                mediaUrl = "https://picsum.photos/seed/story3/900/1400",
                caption = "Coffee time ☕",
                expiresAt = System.currentTimeMillis() + 24 * 3_600_000L,
                createdAt = System.currentTimeMillis() - 3 * 60 * 60_000L
            ),
            StoryEntity(
                id = 0,
                authorId = 104L,
                authorName = "Wendy",
                authorAvatar = "https://picsum.photos/seed/wendy/160/160",
                mediaUrl = "https://picsum.photos/seed/story4/900/1400",
                caption = "Spring is here 🌸",
                expiresAt = System.currentTimeMillis() + 24 * 3_600_000L,
                createdAt = System.currentTimeMillis() - 5 * 60 * 60_000L
            )
        )

        // 为第 1 条动态构造若干评论，便于进入 Comments 页可以看到完整列表 UI
        val sampleComments = listOf(
            CommentEntity(
                id = 0,
                postId = 1L,
                authorId = 201L,
                authorName = "Amelia",
                authorAvatar = "https://picsum.photos/seed/amelia/120/120",
                content = "好美！下次带我一起啊 🙌",
                commentCount = 12,
                likeCount = 340,
                createdAt = System.currentTimeMillis() - 30_000L
            ),
            CommentEntity(
                id = 0,
                postId = 1L,
                authorId = 202L,
                authorName = "Liam",
                authorAvatar = "https://picsum.photos/seed/liam/120/120",
                content = "第三张构图太棒了，能分享一下相机参数吗？",
                commentCount = 3,
                likeCount = 128,
                createdAt = System.currentTimeMillis() - 60_000L
            ),
            CommentEntity(
                id = 0,
                postId = 1L,
                authorId = 203L,
                authorName = "Sophia",
                authorAvatar = "https://picsum.photos/seed/sophia/120/120",
                content = "想去想去！住的哪个酒店？",
                commentCount = 0,
                likeCount = 22,
                createdAt = System.currentTimeMillis() - 2 * 60_000L
            ),
            CommentEntity(
                id = 0,
                postId = 1L,
                authorId = 204L,
                authorName = "Noah",
                authorAvatar = "https://picsum.photos/seed/noah/120/120",
                content = "Bali 真的适合放空，推荐去乌布看一下梯田 🌾",
                commentCount = 2,
                likeCount = 96,
                createdAt = System.currentTimeMillis() - 5 * 60_000L
            )
        )

        localDataSource.savePosts(samplePosts)
        localDataSource.saveStories(sampleStories)
        localDataSource.saveComments(sampleComments)

        Timber.i("[MomentRepository] 已插入 Mock 数据：%d 条动态 / %d 条故事 / %d 条评论",
            samplePosts.size, sampleStories.size, sampleComments.size)
    }
}
