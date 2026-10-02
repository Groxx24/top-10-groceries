package com.top10.groceries.data.repository

import com.top10.groceries.domain.model.Offer

/** Where one store's offers come from. Adding a store means adding one of these. */
interface StoreOfferSource {
    /** Asks the store for everything it has on promotion right now. */
    suspend fun fetchOffers(): List<Offer>
}
