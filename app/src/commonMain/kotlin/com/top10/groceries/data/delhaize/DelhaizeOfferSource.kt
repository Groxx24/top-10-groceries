package com.top10.groceries.data.delhaize

import com.top10.groceries.data.repository.StoreOfferSource
import com.top10.groceries.domain.model.Offer
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class DelhaizeOfferSource(
    private val api: DelhaizeApi,
    private val mapper: DelhaizeOfferMapper,
    /** "fr", "nl" or "en": the language of product names and deal labels. */
    private val language: String,
) : StoreOfferSource {

    override suspend fun fetchOffers(): List<Offer> = coroutineScope {
        // The first page says how many there are; the rest are fetched a few at a time, since
        // each takes seconds and there are a couple of dozen.
        val first = api.promotionPage(language, page = 0)
        val pageCount = first.pagination?.totalPages ?: 1
        val limit = Semaphore(PARALLEL_REQUESTS)
        val rest = (1 until pageCount)
            .map { page -> async { limit.withPermit { api.promotionPage(language, page) } } }
            .awaitAll()

        (listOf(first) + rest)
            .flatMap { it.products }
            .distinctBy { it.code }
            .mapNotNull(mapper::map)
    }

    private companion object {
        const val PARALLEL_REQUESTS = 4
    }
}
