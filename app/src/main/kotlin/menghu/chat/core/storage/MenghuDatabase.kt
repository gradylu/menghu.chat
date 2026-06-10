package menghu.chat.core.storage

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import menghu.chat.feature.auth.model.UserDao
import menghu.chat.feature.auth.model.UserEntity
import menghu.chat.feature.chat.model.ConversationDao
import menghu.chat.feature.chat.model.ConversationEntity
import menghu.chat.feature.chat.model.MessageDao
import menghu.chat.feature.chat.model.MessageEntity
import menghu.chat.feature.friend.model.FriendDao
import menghu.chat.feature.friend.model.FriendEntity
import menghu.chat.feature.friend.model.NearbyUserDao
import menghu.chat.feature.friend.model.NearbyUserEntity
import menghu.chat.feature.moment.model.CommentDao
import menghu.chat.feature.moment.model.CommentEntity
import menghu.chat.feature.moment.model.PostDao
import menghu.chat.feature.moment.model.PostEntity
import menghu.chat.feature.moment.model.StoryDao
import menghu.chat.feature.moment.model.StoryEntity
import menghu.chat.feature.notification.model.NotificationDao
import menghu.chat.feature.notification.model.NotificationEntity
import timber.log.Timber

/**
 * 梦狐社交应用本地 Room 数据库
 * - 聚合所有业务实体表，暴露各 DAO 访问入口
 * - 版本号：2（friend/nearby_user 表字段有扩展）
 * - 使用 fallbackToDestructiveMigration，升级期自动重建表，便于开发阶段迭代
 */
@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        StoryEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        FriendEntity::class,
        NearbyUserEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MenghuDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun storyDao(): StoryDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun friendDao(): FriendDao
    abstract fun nearbyUserDao(): NearbyUserDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        /** 数据库文件名称 */
        const val DB_NAME = "menghu_chat_db"

        /**
         * 使用给定 Context 构建数据库实例
         * - 开发期允许主线程查询（便于调试）
         * - fallbackToDestructiveMigration：版本升级时自动重建表
         */
        fun build(context: Context): MenghuDatabase {
            Timber.d("[MenghuDatabase] 构建 Room 数据库实例")
            return Room.databaseBuilder(
                context.applicationContext,
                MenghuDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}
