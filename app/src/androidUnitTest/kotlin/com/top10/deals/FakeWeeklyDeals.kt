package com.top10.deals

import com.top10.deals.domain.model.LocalizedText
import com.top10.deals.domain.model.WeeklyDeal
import com.top10.deals.domain.repository.WeeklyDealsRepository

/** [count] plain deals, "deal-1" to "deal-[count]", for tests that need deals but not real ones. */
fun fakeDeals(count: Int, validUntil: String? = null): List<WeeklyDeal> = List(count) { index ->
    WeeklyDeal(
        id = "deal-${index + 1}",
        name = LocalizedText.same("Deal ${index + 1}"),
        brand = null,
        packageSize = null,
        label = null,
        price = 1.0,
        validUntil = validUntil,
    )
}

/** Gives every store the same [deals]; the real ones change every week, so tests don't use them. */
class FakeWeeklyDeals(private val deals: List<WeeklyDeal> = fakeDeals(20)) : WeeklyDealsRepository {
    override suspend fun weeklyDeals(storeId: String): List<WeeklyDeal> = deals
}
