package com.top10.groceries.data.delhaize

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

/**
 * The GraphQL endpoint behind delhaize.be/promotions. It is public and needs no key, but it is
 * not a documented API: it answers slowly and may change without notice.
 */
class DelhaizeApi(private val client: HttpClient) {

    /** One page of the products on promotion, [PAGE_SIZE] at a time, counting from 0. */
    suspend fun promotionPage(language: String, page: Int): ProductListDto {
        val response: ProductListResponseDto = client.post(ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(
                buildJsonObject {
                    put("operationName", "ProductList")
                    put("query", QUERY)
                    putJsonObject("variables") {
                        put("productListingType", "PROMOTION_SEARCH")
                        put("lang", language)
                        put("lazyLoadCount", PAGE_SIZE)
                        put("pageNumber", page)
                        put("hideUnavailableProducts", true)
                    }
                },
            )
        }.body()
        return response.data?.productList
            ?: error("Delhaize returned no product list for page $page")
    }

    private companion object {
        const val ENDPOINT = "https://www.delhaize.be/api/v1/?operationName=ProductList"

        /** The largest page the endpoint serves; it fails above this. */
        const val PAGE_SIZE = 50

        val QUERY = """
            query ProductList(${'$'}productListingType:String!${'$'}lang:String${'$'}lazyLoadCount:Int${'$'}pageNumber:Int${'$'}hideUnavailableProducts:Boolean){
              productList(productListingType:${'$'}productListingType lang:${'$'}lang lazyLoadCount:${'$'}lazyLoadCount pageNumber:${'$'}pageNumber hideUnavailableProducts:${'$'}hideUnavailableProducts){
                products{
                  code name manufacturerName url
                  firstLevelCategory{code name}
                  images{format url}
                  price{value supplementaryPriceLabel2}
                  potentialPromotions{code simplePromotionMessage promotionType qualifyingCount redemptionLevel fromDate toDate}
                }
                pagination{totalPages}
              }
            }
        """.trimIndent()
    }
}
