package com.top10.deals.domain.model

/** A store the app has a top list for. [id] is how the list is looked up ("delhaize"). */
data class Store(
    val id: String,
    val name: String,
    /** The store's logo, if there is one; the list shows the name's first letter otherwise. */
    val logoUrl: String? = null,
)
