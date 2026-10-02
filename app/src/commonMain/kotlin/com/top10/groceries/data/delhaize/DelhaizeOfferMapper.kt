package com.top10.groceries.data.delhaize

import com.top10.groceries.domain.model.Offer
import com.top10.groceries.domain.model.Store

class DelhaizeOfferMapper(
    private val promotionParser: DelhaizePromotionParser,
    private val categoryMapper: DelhaizeCategoryMapper,
) {

    /** The offer on [product], or null when none of its promotions is a deal worth comparing. */
    fun map(product: ProductDto): Offer? {
        val priceDto = product.price ?: return null
        val price = priceDto.value ?: return null
        // A product can be in two promotions at once; the deeper one is the offer.
        val deal = product.potentialPromotions.orEmpty()
            .mapNotNull { promotionParser.parse(it, price) }
            .maxByOrNull { it.discount }
            ?: return null

        return Offer(
            store = Store.DELHAIZE,
            productCode = product.code,
            brand = product.manufacturerName?.takeIf { it.isNotBlank() },
            // Delhaize separates the parts of a name with pipes: "Filet de poulet | Nature".
            name = product.name.orEmpty().split('|').joinToString(" ") { it.trim() }.trim(),
            category = categoryMapper.map(product.firstLevelCategory?.code, product.url),
            categoryLabel = product.firstLevelCategory?.name.orEmpty(),
            price = price,
            packageSize = priceDto.supplementaryPriceLabel2?.takeIf { it.isNotBlank() },
            imageUrl = product.images.orEmpty()
                .firstOrNull { it.format == LIST_IMAGE_FORMAT }?.url
                ?.let { SITE + it },
            productUrl = product.url?.let { SITE + it },
            deal = deal,
        )
    }

    private companion object {
        const val SITE = "https://www.delhaize.be"
        const val LIST_IMAGE_FORMAT = "respListGrid"
    }
}
