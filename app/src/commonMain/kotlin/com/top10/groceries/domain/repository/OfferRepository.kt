package com.top10.groceries.domain.repository

import com.top10.groceries.domain.model.Offer
import com.top10.groceries.domain.model.Store

interface OfferRepository {
    /**
     * Every product [store] has on promotion right now. Kept in memory after the first call;
     * [refresh] asks the store again.
     */
    suspend fun currentOffers(store: Store, refresh: Boolean = false): List<Offer>
}
