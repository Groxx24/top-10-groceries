package com.top10.products.domain.model

/** A store the app has a top list for. [id] is how the list is looked up ("delhaize"). */
data class Store(
    val id: String,
    val name: String,
)
