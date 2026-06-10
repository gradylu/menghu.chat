# 混淆规则文件：保留项目核心类与第三方库必要规则

# 保留实体类（避免序列化/反序列化失败）
-keep class menghu.chat.** { *; }
-keepclassmembers class menghu.chat.** { *; }

# Retrofit 相关
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Room
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# DataStore
-keep class androidx.datastore.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class **_HiltModules* { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.InstallIn <methods>;
}

# Coil
-keep class coil.** { *; }
-dontwarn coil.**

# Gson
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keepattributes Signature
