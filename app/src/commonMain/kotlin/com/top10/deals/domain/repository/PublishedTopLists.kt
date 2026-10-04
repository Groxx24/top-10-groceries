package com.top10.deals.domain.repository

import com.top10.deals.domain.model.TopOffers

/** Every published top list at once, for the debug screen to clean up. */
interface PublishedTopLists {
    /** Every list that is published, whichever store it is for. */
    suspend fun all(): List<TopOffers>

    /** Deletes the published list of the store with [storeId]. */
    suspend fun delete(storeId: String)
}
