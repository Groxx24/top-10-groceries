package com.top10.groceries.domain.ranking

import com.top10.groceries.domain.model.Deal
import com.top10.groceries.domain.model.Offer
import com.top10.groceries.domain.model.ProductCategory
import com.top10.groceries.domain.model.Store

/** An offer for tests, where only what the ranking reads has to be given. */
fun offer(
    category: ProductCategory,
    discount: Double,
    quantity: Int = 1,
    price: Double = 5.0,
    code: String = "P-$category-$discount-$quantity-$price",
    promotionId: String = "promo-$code",
) = Offer(
    store = Store.DELHAIZE,
    productCode = code,
    brand = null,
    name = code,
    category = category,
    categoryLabel = category.name,
    price = price,
    packageSize = null,
    imageUrl = null,
    productUrl = null,
    deal = Deal(
        promotionId = promotionId,
        label = "",
        discount = discount,
        requiredQuantity = quantity,
        needsLoyaltyCard = false,
        validUntil = null,
    ),
)
