package menghu.chat.feature.moment.datasource

import kotlinx.coroutines.flow.Flow
import menghu.chat.feature.moment.model.CommentDao
import menghu.chat.feature.moment.model.CommentEntity
import menghu.chat.feature.moment.model.PostDao
import menghu.chat.feature.moment.model.PostEntity
import menghu.chat.feature.moment.model.StoryDao
import menghu.chat.feature.moment.model.StoryEntity
import timber.log.Timber
import javax.inject.Inject

/**
 * 动态（帖子）本地数据源
 * 封装 Room 数据库对动态、故事、评论的增删查操作，作为 Repository 唯一数据来源
 *
 * @param postDao 动态 DAO
 * @param storyDao 故事 DAO
 * @param commentDao 评论 DAO
 */
class MomentLocalDataSource @Inject constructor(
    private val postDao: PostDao,
    private val storyDao: StoryDao,
    private val commentDao: CommentDao
) {

    // ======================= 动态（Post） =======================

    /** 保存单条动态 */
    suspend fun savePost(post: PostEntity) {
        Timber.d("[MomentLocalDataSource] 保存动态: id=%s", post.id)
        postDao.insertPost(post)
    }

    /** 批量保存动态 */
    suspend fun savePosts(posts: List<PostEntity>) {
        Timber.d("[MomentLocalDataSource] 批量保存动态: %d 条", posts.size)
        postDao.insertPosts(posts)
    }

    /** 流式获取所有动态（按时间倒序） */
    fun observePosts(): Flow<List<PostEntity>> = postDao.getAllPosts()

    /** 一次性获取所有动态 */
    suspend fun getAllPosts(): List<PostEntity> = postDao.getAllPostsList()

    /** 根据 ID 获取单条动态 */
    suspend fun getPostById(id: Long): PostEntity? = postDao.getPostById(id)

    /** 删除一条动态 */
    suspend fun deletePost(id: Long) = postDao.deletePost(id)

    /** 清空所有动态 */
    suspend fun clearAllPosts() = postDao.clearAll()

    // ======================= 故事（Story） =======================

    /** 保存单条故事 */
    suspend fun saveStory(story: StoryEntity) {
        Timber.d("[MomentLocalDataSource] 保存故事: id=%s", story.id)
        storyDao.insertStory(story)
    }

    /** 批量保存故事 */
    suspend fun saveStories(stories: List<StoryEntity>) {
        Timber.d("[MomentLocalDataSource] 批量保存故事: %d 条", stories.size)
        storyDao.insertStories(stories)
    }

    /** 流式获取所有故事（按时间倒序） */
    fun observeStories(): Flow<List<StoryEntity>> = storyDao.getAllStories()

    /** 一次性获取所有故事 */
    suspend fun getAllStories(): List<StoryEntity> = storyDao.getAllStoriesList()

    /** 清空所有故事 */
    suspend fun clearAllStories() = storyDao.clearAll()

    // ======================= 评论（Comment） =======================

    /** 保存单条评论 */
    suspend fun saveComment(comment: CommentEntity) {
        Timber.d("[MomentLocalDataSource] 保存评论: postId=%s", comment.postId)
        commentDao.insertComment(comment)
    }

    /** 批量保存评论 */
    suspend fun saveComments(comments: List<CommentEntity>) {
        Timber.d("[MomentLocalDataSource] 批量保存评论: %d 条", comments.size)
        commentDao.insertComments(comments)
    }

    /** 流式获取某条动态下的所有评论（按时间倒序） */
    fun observeComments(postId: Long): Flow<List<CommentEntity>> =
        commentDao.getCommentsByPost(postId)

    /** 一次性获取某条动态下的所有评论 */
    suspend fun getCommentsByPost(postId: Long): List<CommentEntity> =
        commentDao.getCommentsByPostList(postId)

    /** 清空所有评论 */
    suspend fun clearAllComments() = commentDao.clearAll()
}
