package com.top10.groceries.data.repository

import com.top10.groceries.domain.model.Offer
import com.top10.groceries.domain.model.Store
import com.top10.groceries.domain.repository.OfferRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class OfferRepositoryImpl(
    private val sources: Map<Store, StoreOfferSource>,
) : OfferRepository {

    private val cache = mutableMapOf<Store, List<Offer>>()

    // One fetch at a time: a second caller waits for the first and then reads the cache.
    private val mutex = Mutex()

    override suspend fun currentOffers(store: Store, refresh: Boolean): List<Offer> = mutex.withLock {
        if (!refresh) cache[store]?.let { return it }
        val source = sources[store] ?: error("No offer source for $store")
        source.fetchOffers().also { cache[store] = it }
    }
}
