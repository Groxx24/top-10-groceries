package com.top10.deals.domain.repository

import com.top10.deals.domain.model.TopOffers

interface TopOffersRepository {
    /** This week's top list for the store with [storeId]. Throws when there is no such store. */
    suspend fun topOffers(storeId: String): TopOffers

    /** Every store's top list that has offers this week, read at once; a store with none is left out. */
    suspend fun allTopOffers(): List<TopOffers>
}
