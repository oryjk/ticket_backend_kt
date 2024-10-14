package com.wangrui.ticketsystem.utils

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Created on 2024/4/7 19:31.
 * @author wangrui
 * @since 0.0.1
 */

object DateUtils {
    fun isLastThursdayOfMonth(date: LocalDate): Boolean {
        // 获取这个月的最后一天
        val lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth())

        // 找到这个月最后一个星期四的日期
        val lastThursdayOfMonth = lastDayOfMonth.with(DayOfWeek.THURSDAY)

        // 如果这个月的最后一天是在星期四之后，那么我们需要向前推到前一个星期四
        val finalThursday = if (lastThursdayOfMonth.isAfter(lastDayOfMonth)) {
            lastThursdayOfMonth.minusWeeks(1)
        } else {
            lastThursdayOfMonth
        }

        // 检查给定的日期是否是这个月的最后一个星期四
        return date.isEqual(finalThursday)
    }
}