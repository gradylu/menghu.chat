// 根级 build.gradle.kts：声明所有子模块会用到的插件（apply false 表示不在根项目自身 apply）
// 版本统一由 gradle/libs.versions.toml [plugins] 段管理，子模块通过 alias(libs.plugins.xxx) 引用
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
}

