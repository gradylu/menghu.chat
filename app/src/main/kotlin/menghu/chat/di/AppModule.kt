package menghu.chat.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import menghu.chat.core.network.ApiService
import menghu.chat.core.network.MockInterceptor
import menghu.chat.core.network.RetrofitClient
import menghu.chat.core.storage.MenghuDatabase
import menghu.chat.core.storage.UserPreferences
import menghu.chat.feature.auth.model.UserDao
import menghu.chat.feature.chat.model.ConversationDao
import menghu.chat.feature.chat.model.MessageDao
import menghu.chat.feature.friend.model.FriendDao
import menghu.chat.feature.friend.model.NearbyUserDao
import menghu.chat.feature.moment.model.CommentDao
import menghu.chat.feature.moment.model.PostDao
import menghu.chat.feature.moment.model.StoryDao
import menghu.chat.feature.notification.model.NotificationDao
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt 全局依赖注入模块
 * 提供 OkHttp、Retrofit、API 服务、Room 数据库、各 DAO、DataStore、UserPreferences 等单例
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // DataStore 全局扩展属性（避免重复创建）
    private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "menghu_chat_user_prefs"
    )

    /** 提供 OkHttp 客户端（含 MockInterceptor + 日志拦截器） */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Timber.d("[OkHttp] %s", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(MockInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /** 提供 Retrofit 实例 */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return RetrofitClient.provideRetrofit(okHttpClient)
    }

    /** 提供 ApiService 接口 */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    /** 提供 Room 数据库实例 */
    @Provides
    @Singleton
    fun provideMenghuDatabase(@ApplicationContext context: Context): MenghuDatabase {
        return Room.databaseBuilder(
            context,
            MenghuDatabase::class.java,
            MenghuDatabase.DB_NAME
        )
            // 首次启动允许破坏性迁移（开发阶段简化，生产环境需提供正式 Migration）
            .fallbackToDestructiveMigration()
            .build()
    }

    /** 提供 UserDao */
    @Provides
    @Singleton
    fun provideUserDao(db: MenghuDatabase): UserDao = db.userDao()

    /** 提供 PostDao */
    @Provides
    @Singleton
    fun providePostDao(db: MenghuDatabase): PostDao = db.postDao()

    /** 提供 CommentDao */
    @Provides
    @Singleton
    fun provideCommentDao(db: MenghuDatabase): CommentDao = db.commentDao()

    /** 提供 StoryDao */
    @Provides
    @Singleton
    fun provideStoryDao(db: MenghuDatabase): StoryDao = db.storyDao()

    /** 提供 ConversationDao */
    @Provides
    @Singleton
    fun provideConversationDao(db: MenghuDatabase): ConversationDao = db.conversationDao()

    /** 提供 MessageDao */
    @Provides
    @Singleton
    fun provideMessageDao(db: MenghuDatabase): MessageDao = db.messageDao()

    /** 提供 FriendDao */
    @Provides
    @Singleton
    fun provideFriendDao(db: MenghuDatabase): FriendDao = db.friendDao()

    /** 提供 NearbyUserDao */
    @Provides
    @Singleton
    fun provideNearbyUserDao(db: MenghuDatabase): NearbyUserDao = db.nearbyUserDao()

    /** 提供 NotificationDao */
    @Provides
    @Singleton
    fun provideNotificationDao(db: MenghuDatabase): NotificationDao = db.notificationDao()

    /** 提供 DataStore（Preferences） */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.appDataStore
    }

    /** 提供 UserPreferences */
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}
