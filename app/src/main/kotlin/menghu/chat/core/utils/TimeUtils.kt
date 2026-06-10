package menghu.chat.core.utils

import java.util.concurrent.TimeUnit

/**
 * 时间格式化工具
 * 将毫秒时间戳转换为相对时间文本（如 "刚刚" / "2 分钟前" / "1 天前"）
 */
object TimeUtils {

    /**
     * 格式化相对时间
     * @param epochMillis 毫秒时间戳（相对 1970）
     * @return 友好的相对时间字符串
     */
    fun formatRelativeTime(epochMillis: Long): String {
        if (epochMillis <= 0) return "刚刚"

        val now = System.currentTimeMillis()
        val diff = now - epochMillis

        // 小于 1 分钟
        if (diff < TimeUnit.MINUTES.toMillis(1)) {
            return "刚刚"
        }
        // 小于 1 小时
        if (diff < TimeUnit.HOURS.toMillis(1)) {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            return "${minutes} 分钟前"
        }
        // 小于 1 天
        if (diff < TimeUnit.DAYS.toMillis(1)) {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            return "${hours} 小时前"
        }
        // 小于 7 天
        if (diff < TimeUnit.DAYS.toMillis(7)) {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            return "${days} 天前"
        }
        // 超过 7 天，按周显示
        val weeks = TimeUnit.MILLISECONDS.toDays(diff) / 7
        return if (weeks < 4) {
            "${weeks} 周前"
        } else {
            val months = weeks / 4
            if (months < 12) {
                "${months} 个月前"
            } else {
                "${months / 12} 年前"
            }
        }
    }

    /**
     * 返回英文分组标题：Today / Yesterday / Earlier
     * 供 SectionHeader 使用
     */
    fun formatGroupTitle(epochMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - epochMillis
        val oneDay = TimeUnit.DAYS.toMillis(1)

        return when {
            diff < oneDay -> "Today"
            diff < 2 * oneDay -> "Yesterday"
            else -> "Earlier"
        }
    }
}

/** 顶层扩展函数：Long 格式化相对时间 */
fun Long.toRelativeTime(): String = TimeUtils.formatRelativeTime(this)

/** 顶层扩展函数：Long 格式化分组标题 */
fun Long.toGroupTitle(): String = TimeUtils.formatGroupTitle(this)
