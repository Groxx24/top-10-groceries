package com.top10.deals.domain.usecase

import com.top10.deals.FakeWeeklyDeals
import com.top10.deals.data.hardcoded.HardcodedCatalog
import com.top10.deals.domain.model.CANDIDATE_COUNT
import com.top10.deals.domain.model.LocalizedText
import com.top10.deals.domain.model.WeeklyDeal
import com.top10.deals.domain.model.discount
import com.top10.deals.fakeDeals
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetWeeklyDealsUseCaseTest {

    @Test
    fun `ended deals are dropped and the next ones take their place`() = runTest {
        val ended = fakeDeals(5, validUntil = "10/10").map { it.copy(id = "ended-${it.id}") }
        val current = fakeDeals(30, validUntil = "17/10")
        val getWeeklyDeals = GetWeeklyDealsUseCase(HardcodedCatalog(), FakeWeeklyDeals(ended + current)) { LocalDate(2026, 10, 11) }

        assertEquals(current.take(CANDIDATE_COUNT), getWeeklyDeals("lidl").deals)
    }

    @Test
    fun `a deal is still a candidate on its last day`() = runTest {
        val deals = fakeDeals(3, validUntil = "10/10")
        val getWeeklyDeals = GetWeeklyDealsUseCase(HardcodedCatalog(), FakeWeeklyDeals(deals)) { LocalDate(2026, 10, 10) }

        assertEquals(deals, getWeeklyDeals("lidl").deals)
    }

    @Test
    fun `the discount is the share of the regular price saved`() {
        val deal = WeeklyDeal(id = "a", name = LocalizedText.same("Avocados"), brand = null, packageSize = null, label = null,
            price = 2.58, regularPrice = 3.87, validUntil = null)
        assertEquals(0.333, deal.discount!!, 0.001)
        assertNull(deal.copy(regularPrice = null).discount)
    }
}
