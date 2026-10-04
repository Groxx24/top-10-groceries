package com.top10.products.domain.usecase

import com.top10.products.domain.model.CANDIDATE_COUNT
import com.top10.products.domain.model.WeeklyDeals
import com.top10.products.domain.repository.StoreRepository
import com.top10.products.domain.repository.store
import com.top10.products.domain.repository.WeeklyDealsRepository

/** The [CANDIDATE_COUNT] most relevant deals of the week at one store, to pick its top list from. */
class GetWeeklyDealsUseCase(
    private val stores: StoreRepository,
    private val deals: WeeklyDealsRepository,
) {
    suspend operator fun invoke(storeId: String): WeeklyDeals {
        val store = stores.store(storeId)
        return WeeklyDeals(store = store, deals = deals.weeklyDeals(storeId).take(CANDIDATE_COUNT))
    }
}
