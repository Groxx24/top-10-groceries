package com.top10.deals.domain.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

private val DAY_MONTH = Regex("""(\d{1,2})/(\d{1,2})""")

/**
 * The last day of a deal printed as "07/10" (day/month, no year), in the year of [fetchedOn], or
 * the next year when that would put it more than half a year before [fetchedOn] (a "05/01" read in
 * December). Null for anything else.
 */
internal fun dueDate(validUntil: String, fetchedOn: LocalDate): LocalDate? {
    val (day, month) = DAY_MONTH.matchEntire(validUntil.trim())?.destructured ?: return null
    fun inYear(year: Int) = runCatching { LocalDate(year, month.toInt(), day.toInt()) }.getOrNull()
    val thisYear = inYear(fetchedOn.year) ?: return null
    return if (thisYear < fetchedOn.minus(6, DateTimeUnit.MONTH)) inYear(fetchedOn.year + 1) else thisYear
}
