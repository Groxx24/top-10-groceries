package com.top10.deals.domain.model

/**
 * What kind of product a deal is, which the app draws as a picture in place of a product photo.
 * Published top lists store the entry's [name], so renaming one breaks lists already in Firestore.
 */
enum class ProductCategory {
    FRUIT,
    VEGETABLES,
    MEAT,
    POULTRY,
    FISH,
    EGGS,
    DAIRY,
    CHEESE,
    BAKERY,
    SWEETS,
    PASTA,
    CANNED,
    FROZEN,
    ICE_CREAM,
    SNACKS,
    SOFT_DRINKS,
    WATER,
    COFFEE_TEA,
    BEER,
    WINE,
    SPIRITS,
    LAUNDRY,
    CLEANING,
    PAPER,
    PERSONAL_CARE,
    BABY,
    PET,

    /** Anything else, and any category this version of the app does not know. */
    OTHER,
    ;

    companion object {
        /** The category stored as [name], or [OTHER] for a missing or unknown one. */
        fun fromName(name: String?): ProductCategory = entries.firstOrNull { it.name == name } ?: OTHER
    }
}
