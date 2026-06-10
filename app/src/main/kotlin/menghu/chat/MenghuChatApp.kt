package menghu.chat

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * 应用入口 Application 类
 * - 初始化 Hilt 依赖注入框架
 * - 初始化 Timber 日志工具
 * - 配置 Coil 全局图片加载器（25% 内存缓存、100MB 磁盘缓存、自定义 OkHttp 超时）
 */
@HiltAndroidApp
class MenghuChatApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        // 初始化 Timber 日志
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("MenghuChatApp 已启动，应用包名=%s，版本=%s", packageName, BuildConfig.VERSION_NAME)
    }

    /**
     * Coil 全局 ImageLoader 工厂方法
     * - 内存缓存：占用最大可用内存的 25%
     * - 磁盘缓存：100MB 容量（位于应用 cache/image_cache 目录）
     * - 默认启用内存/磁盘缓存，使用自定义 OkHttp 超时
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            // 内存缓存配置（25% 可用内存）
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            // 磁盘缓存配置（100MB）
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100L * 1024 * 1024)
                    .build()
            }
            // OkHttp 客户端（使用标准连接池，30 秒超时）
            .okHttpClient(
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            // 默认缓存策略
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }
}
