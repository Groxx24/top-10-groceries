package com.top10.groceries.domain.repository

import com.top10.groceries.domain.model.Store

interface StoreRepository {
    /** Every store that has a top list, in the order to show them. */
    suspend fun stores(): List<Store>
}
