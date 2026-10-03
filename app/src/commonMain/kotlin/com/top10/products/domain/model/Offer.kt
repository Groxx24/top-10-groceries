package com.top10.products.domain.model

/**
 * One place in a store's published top list: a deal from that week's folder and where it was
 * ranked. The list is ranked before it reaches the app, so nothing here is computed on the device.
 */
data class Offer(
    /** 1 for the best offer of the week. */
    val rank: Int,
    val deal: WeeklyDeal,
)

/** A store's top list for the week. */
data class TopOffers(
    val store: Store,
    /** Best first; empty when no list has been published for the store. */
    val offers: List<Offer>,
)
