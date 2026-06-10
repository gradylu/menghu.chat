package menghu.chat.core.utils

/**
 * 通用扩展函数集合
 * 包括字符串校验、空值处理等常用工具
 */

/** 验证是否为有效邮箱格式 */
fun String?.isValidEmail(): Boolean {
    if (this.isNullOrEmpty()) return false
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return emailRegex.matches(this)
}

/** 空字符串时返回 0L */
fun Long?.orZero(): Long = this ?: 0L

/** 空字符串时返回 0 */
fun Int?.orZero(): Int = this ?: 0

/** 空字符串时返回 0.0 */
fun Double?.orZero(): Double = this ?: 0.0

/** 空字符串时返回 0.0f */
fun Float?.orZero(): Float = this ?: 0.0f

/** null 或空白时返回默认值 */
fun String?.orDefault(default: String): String = if (this.isNullOrBlank()) default else this

/** 判断字符串是否为有效密码（至少 6 位） */
fun String?.isValidPassword(): Boolean {
    if (this.isNullOrEmpty()) return false
    return this.length >= 6
}

/** 判断字符串是否为有效昵称（1-20 个字符） */
fun String?.isValidNickname(): Boolean {
    if (this.isNullOrBlank()) return false
    return this.length in 1..20
}

/** 限制字符串最大长度，超出部分截断 */
fun String?.truncate(maxLength: Int, ellipsis: String = "…"): String {
    if (this == null) return ""
    return if (this.length > maxLength) {
        this.take(maxLength) + ellipsis
    } else {
        this
    }
}

/** 判断集合是否为空 */
fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean = this != null && this.isNotEmpty()

/** 将图片 URL 列表字符串转为数组（以英文逗号分隔） */
fun String?.toImageList(): List<String> {
    if (this.isNullOrBlank()) return emptyList()
    return this.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
