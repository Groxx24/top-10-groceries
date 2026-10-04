package com.top10.deals.data.hardcoded

import com.top10.deals.domain.model.CANDIDATE_COUNT
import com.top10.deals.domain.model.LocalizedText
import com.top10.deals.domain.model.ProductCategory
import com.top10.deals.domain.model.WeeklyDeal
import com.top10.deals.domain.model.discount
import com.top10.deals.domain.usecase.GetWeeklyDealsUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HardcodedWeeklyDealsTest {

    private val catalog = HardcodedCatalog()
    // Thursday 1 October 2026, in the week the deals were typed in for.
    private val getWeeklyDeals = GetWeeklyDealsUseCase(catalog, HardcodedWeeklyDeals()) { LocalDate(2026, 10, 1) }

    @Test
    fun `every store has 20 candidates with distinct ids and names`() = runTest {
        for (store in catalog.stores()) {
            val candidates = getWeeklyDeals(store.id)
            assertEquals(store, candidates.store)
            assertEquals(CANDIDATE_COUNT, candidates.deals.size)
            assertEquals(CANDIDATE_COUNT, candidates.deals.distinctBy { it.id }.size)
            assertEquals(CANDIDATE_COUNT, candidates.deals.distinctBy { it.name to it.brand }.size)
        }
    }

    @Test
    fun `every candidate is written in English, French and Dutch`() = runTest {
        for (store in catalog.stores()) {
            for (deal in getWeeklyDeals(store.id).deals) {
                for (text in listOfNotNull(deal.name, deal.packageSize, deal.label)) {
                    assertTrue("${deal.id}: $text", listOf(text.en, text.fr, text.nl).none { it.isBlank() })
                }
            }
        }
    }

    @Test
    fun `every candidate has a category to draw`() = runTest {
        for (store in catalog.stores()) {
            for (deal in getWeeklyDeals(store.id).deals) {
                assertTrue(deal.id, deal.category != ProductCategory.OTHER)
            }
        }
    }

    @Test
    fun `the discount is the share of the regular price saved`() {
        val deal = WeeklyDeal(id = "a", name = LocalizedText.same("Avocados"), brand = null, packageSize = null, label = null,
            price = 2.58, regularPrice = 3.87, validUntil = null)
        assertEquals(0.333, deal.discount!!, 0.001)
        assertNull(deal.copy(regularPrice = null).discount)
    }

    @Test
    fun `ended deals are dropped and the next ones take their place`() = runTest {
        val repository = HardcodedWeeklyDeals()
        // Sunday 4 October: the deals valid until 03/10 have ended.
        val later = GetWeeklyDealsUseCase(catalog, repository) { LocalDate(2026, 10, 4) }

        val lidl = later("lidl").deals
        assertTrue(lidl.none { it.validUntil == "03/10" })
        assertEquals(repository.weeklyDeals("lidl").filter { it.validUntil != "03/10" }.take(CANDIDATE_COUNT), lidl)
        // Friday 9 October: ALDI's offers valid until 08/10 have ended, those until 10/10 have not.
        val friday = GetWeeklyDealsUseCase(catalog, repository) { LocalDate(2026, 10, 9) }.invoke("aldi").deals
        assertTrue(friday.isNotEmpty() && friday.all { it.validUntil == "10/10" })
    }

    @Test
    fun `an unknown store id fails`() = runTest {
        assertTrue(runCatching { getWeeklyDeals("nope") }.isFailure)
    }
}
