package com.top10.deals.domain.model

import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DueDateTest {

    @Test
    fun `a day and month is read in the year it was fetched`() {
        assertEquals(LocalDate(2026, 10, 7), dueDate("07/10", fetchedOn = LocalDate(2026, 10, 1)))
        assertEquals(LocalDate(2026, 9, 28), dueDate("28/09", fetchedOn = LocalDate(2026, 10, 1)))
    }

    @Test
    fun `an early date read in December is next year`() {
        assertEquals(LocalDate(2027, 1, 5), dueDate("05/01", fetchedOn = LocalDate(2026, 12, 28)))
    }

    @Test
    fun `anything else gives no date`() {
        val fetchedOn = LocalDate(2026, 10, 1)
        assertNull(dueDate("31/02", fetchedOn))
        assertNull(dueDate("next week", fetchedOn))
        assertNull(dueDate("", fetchedOn))
    }
}
