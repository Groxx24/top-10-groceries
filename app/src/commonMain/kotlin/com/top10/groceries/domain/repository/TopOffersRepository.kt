package com.top10.groceries.domain.repository

import com.top10.groceries.domain.model.TopOffers

interface TopOffersRepository {
    /** This week's top list for the store with [storeId]. Throws when there is no such store. */
    suspend fun topOffers(storeId: String): TopOffers
}
