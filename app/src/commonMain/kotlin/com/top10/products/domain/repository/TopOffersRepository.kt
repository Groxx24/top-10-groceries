package com.top10.products.domain.repository

import com.top10.products.domain.model.TopOffers

interface TopOffersRepository {
    /** This week's top list for the store with [storeId]. Throws when there is no such store. */
    suspend fun topOffers(storeId: String): TopOffers
}
