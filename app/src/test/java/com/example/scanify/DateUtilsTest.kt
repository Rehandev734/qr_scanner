package com.example.scanify

import com.example.scanify.core.utils.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    @Test
    fun testStartAndEndOfDay() {
        val now = System.currentTimeMillis()
        val start = DateUtils.getStartOfDay(now)
        val end = DateUtils.getEndOfDay(now)

        assertTrue(start < end)
        assertTrue(now in start..end)

        val cal = Calendar.getInstance().apply { timeInMillis = start }
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
        assertEquals(0, cal.get(Calendar.SECOND))
        assertEquals(0, cal.get(Calendar.MILLISECOND))

        cal.timeInMillis = end
        assertEquals(23, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, cal.get(Calendar.MINUTE))
        assertEquals(59, cal.get(Calendar.SECOND))
        assertEquals(999, cal.get(Calendar.MILLISECOND))
    }

    @Test
    fun testDateCategoryToday() {
        val now = System.currentTimeMillis()
        val category = DateUtils.getDateCategory(now)
        assertEquals("Today", category)
    }

    @Test
    fun testFormatFullNotNull() {
        val now = System.currentTimeMillis()
        val formatted = DateUtils.formatFull(now)
        assertNotNull(formatted)
        assertTrue(formatted.isNotEmpty())
    }

    @Test
    fun testFormatShortNotNull() {
        val now = System.currentTimeMillis()
        val formatted = DateUtils.formatShort(now)
        assertNotNull(formatted)
        assertTrue(formatted.isNotEmpty())
    }

    @Test
    fun testFormatDateOnlyNotNull() {
        val now = System.currentTimeMillis()
        val formatted = DateUtils.formatDateOnly(now)
        assertNotNull(formatted)
        assertTrue(formatted.isNotEmpty())
    }
}
