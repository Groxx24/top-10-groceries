package com.top10.deals.domain.repository

import com.top10.deals.domain.model.TopOffers

interface TopListPublisher {
    /** Publishes [top] as its store's top list, replacing the one there. */
    suspend fun publish(top: TopOffers)
}
