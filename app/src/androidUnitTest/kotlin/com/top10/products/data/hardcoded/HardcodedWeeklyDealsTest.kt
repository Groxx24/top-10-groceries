package com.top10.products.data.hardcoded

import com.top10.products.domain.model.CANDIDATE_COUNT
import com.top10.products.domain.model.WeeklyDeal
import com.top10.products.domain.model.discount
import com.top10.products.domain.usecase.GetWeeklyDealsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HardcodedWeeklyDealsTest {

    private val catalog = HardcodedCatalog()
    private val getWeeklyDeals = GetWeeklyDealsUseCase(catalog, HardcodedWeeklyDeals())

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
    fun `the discount is the share of the regular price saved`() {
        val deal = WeeklyDeal(id = "a", name = "Avocados", brand = null, packageSize = null, label = null,
            price = 2.58, regularPrice = 3.87, validUntil = null)
        assertEquals(0.333, deal.discount!!, 0.001)
        assertNull(deal.copy(regularPrice = null).discount)
    }

    @Test
    fun `an unknown store id fails`() = runTest {
        assertTrue(runCatching { getWeeklyDeals("nope") }.isFailure)
    }
}
