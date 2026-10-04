package com.top10.deals.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.top10.deals.domain.model.LocalizedText

/** When a store's top list was last read. A row with no offers is a store with no list published. */
@Entity(tableName = "top_lists")
data class CachedTopListEntity(
    @PrimaryKey val storeId: String,
    val fetchedAtMillis: Long,
    /** The first day, in epoch days, that one of its deals has ended; null when none gives an end. */
    val expiresOnEpochDay: Long?,
)

/** One place in a cached top list, with what the top list screen shows for the deal. */
@Entity(tableName = "top_offers", primaryKeys = ["storeId", "rank"])
data class CachedOfferEntity(
    val storeId: String,
    val rank: Int,
    @Embedded(prefix = "name_") val name: LocalizedText,
    val brand: String?,
    @Embedded(prefix = "package_size_") val packageSize: LocalizedText?,
    @Embedded(prefix = "label_") val label: LocalizedText?,
    val price: Double?,
    val priceUnit: String?,
    val regularPrice: Double?,
    val needsLoyaltyCard: Boolean,
    /** A `ProductCategory` name. */
    val category: String,
    /** A `GenericProduct` name, or null when the deal has no photo. */
    val product: String?,
    val validUntil: String?,
)
