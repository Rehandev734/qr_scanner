package com.example.scanify.core.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val fullFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val shortFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    private val dateOnlyFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun formatFull(timestamp: Long): String = fullFormat.format(Date(timestamp))
    fun formatShort(timestamp: Long): String = shortFormat.format(Date(timestamp))
    fun formatDateOnly(timestamp: Long): String = dateOnlyFormat.format(Date(timestamp))

    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun getStartOfYesterday(): Long {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getEndOfYesterday(): Long {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun getStartOfLast7Days(): Long {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getStartOfLast30Days(): Long {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -30)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getStartOfMonth(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getDateCategory(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val startOfToday = getStartOfDay(now)
        val startOfYesterday = getStartOfYesterday()
        val startOfLast7Days = getStartOfLast7Days()
        val startOfMonth = getStartOfMonth()

        return when {
            timestamp >= startOfToday -> "Today"
            timestamp >= startOfYesterday -> "Yesterday"
            timestamp >= startOfLast7Days -> "Last 7 Days"
            timestamp >= startOfMonth -> "Last Month"
            else -> "Older"
        }
    }

    fun groupByDate(items: List<Any>, timestampSelector: (Any) -> Long): Map<String, List<Any>> {
        return items.groupBy { getDateCategory(timestampSelector(it)) }
    }
}
