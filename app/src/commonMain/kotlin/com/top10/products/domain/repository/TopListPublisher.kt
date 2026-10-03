package com.top10.products.domain.repository

import com.top10.products.domain.model.TopOffers

interface TopListPublisher {
    /** Publishes [top] as its store's top list, replacing the one there. */
    suspend fun publish(top: TopOffers)
}
