package com.top10.deals.domain.repository

import com.top10.deals.domain.model.Store

interface StoreRepository {
    /** Every store that has a top list, in the order to show them. */
    suspend fun stores(): List<Store>
}

/** The store with [storeId]; asking for one that is not in [StoreRepository.stores] is a bug. */
suspend fun StoreRepository.store(storeId: String): Store =
    stores().firstOrNull { it.id == storeId } ?: error("Unknown store $storeId")
