package menghu.chat.core.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit 客户端单例
 * 提供 Retrofit 实例构建方法，底层 OkHttp 走 MockInterceptor
 * baseUrl 仅为占位符，所有真实请求均被拦截器拦截返回 Mock 数据
 */
object RetrofitClient {

    // Mock 场景下 baseUrl 可随意填写，实际请求不发往该地址
    private const val BASE_URL = "https://api.menghu.chat/"

    /**
     * 构建 Retrofit 实例
     * @param okHttpClient OkHttp 客户端（应由 Hilt 注入，含 MockInterceptor）
     */
    fun provideRetrofit(okHttpClient: okhttp3.OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
