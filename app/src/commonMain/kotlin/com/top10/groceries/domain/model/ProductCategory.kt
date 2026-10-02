package com.top10.groceries.domain.model

/**
 * Store-independent grouping of products. Each store maps its own category tree onto this, and
 * ranking only ever looks at these values.
 */
enum class ProductCategory {
    MEAT_FISH,
    BREAD,
    FRUIT_VEGETABLES,
    VEGETARIAN,
    DAIRY_EGGS,
    READY_MEALS,
    FROZEN,
    PANTRY,
    CANNED,
    SPECIAL_DIET,
    DRINKS,
    SWEETS_PASTRY,
    HYGIENE,
    HOUSEHOLD,
    ALCOHOL,
    HOME,
    BABY,
    PET,
    OTHER,
}
