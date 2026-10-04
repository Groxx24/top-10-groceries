package com.top10.deals.domain.usecase

import com.top10.deals.domain.model.CANDIDATE_COUNT
import com.top10.deals.domain.model.WeeklyDeals
import com.top10.deals.domain.model.hasEnded
import com.top10.deals.domain.repository.StoreRepository
import com.top10.deals.domain.repository.store
import com.top10.deals.domain.repository.WeeklyDealsRepository
import kotlinx.datetime.LocalDate

/**
 * The [CANDIDATE_COUNT] most relevant deals at one store that have not ended on [today], to pick
 * its top list from. Ended ones are dropped before counting, so the next deals take their place;
 * there can be fewer when the store has not that many left.
 */
class GetWeeklyDealsUseCase(
    private val stores: StoreRepository,
    private val deals: WeeklyDealsRepository,
    private val today: () -> LocalDate,
) {
    suspend operator fun invoke(storeId: String): WeeklyDeals {
        val store = stores.store(storeId)
        val today = today()
        val current = deals.weeklyDeals(storeId).filterNot { it.hasEnded(today) }
        return WeeklyDeals(store = store, deals = current.take(CANDIDATE_COUNT))
    }
}
