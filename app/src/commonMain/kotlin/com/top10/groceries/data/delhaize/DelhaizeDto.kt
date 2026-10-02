package com.top10.groceries.data.delhaize

import kotlinx.serialization.Serializable

// The part of Delhaize's GraphQL answer the app reads. Field names are the API's own.

@Serializable
data class ProductListResponseDto(val data: DataDto? = null) {
    @Serializable
    data class DataDto(val productList: ProductListDto? = null)
}

@Serializable
data class ProductListDto(
    val products: List<ProductDto> = emptyList(),
    val pagination: PaginationDto? = null,
)

@Serializable
data class PaginationDto(val totalPages: Int = 0)

@Serializable
data class ProductDto(
    val code: String,
    val name: String? = null,
    val manufacturerName: String? = null,
    /** Path of the product page. Its segments after "shop" are the category tree. */
    val url: String? = null,
    val firstLevelCategory: CategoryDto? = null,
    val images: List<ImageDto>? = null,
    val price: PriceDto? = null,
    val potentialPromotions: List<PromotionDto>? = null,
)

@Serializable
data class CategoryDto(val code: String? = null, val name: String? = null)

@Serializable
data class ImageDto(val format: String? = null, val url: String? = null)

@Serializable
data class PriceDto(
    /** Normal price of one item in euros. For products sold by weight, of one average piece. */
    val value: Double? = null,
    /** Pack size: "500 gr", "6 x 25 cl". */
    val supplementaryPriceLabel2: String? = null,
)

@Serializable
data class PromotionDto(
    val code: String? = null,
    /** The wording on the shelf label, in the requested language: "1+1_gratis", "2ème à -50%". */
    val simplePromotionMessage: String? = null,
    /** Not translated. One of the values in [DelhaizePromotionParser]. */
    val promotionType: String? = null,
    /** How many items have to be bought. */
    val qualifyingCount: Int? = null,
    /** "MEMBER" when a SuperPlus card is needed, "MASS" when it is not. */
    val redemptionLevel: String? = null,
    /** "dd/MM" or "dd/MM/yy". */
    val fromDate: String? = null,
    val toDate: String? = null,
)
