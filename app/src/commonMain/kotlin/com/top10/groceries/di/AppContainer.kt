package com.top10.groceries.di

import com.top10.groceries.data.delhaize.DelhaizeApi
import com.top10.groceries.data.delhaize.DelhaizeCategoryMapper
import com.top10.groceries.data.delhaize.DelhaizeOfferMapper
import com.top10.groceries.data.delhaize.DelhaizeOfferSource
import com.top10.groceries.data.delhaize.DelhaizePromotionParser
import com.top10.groceries.data.repository.OfferRepositoryImpl
import com.top10.groceries.domain.model.Store
import com.top10.groceries.domain.ranking.OfferScorer
import com.top10.groceries.domain.ranking.RankingPolicy
import com.top10.groceries.domain.ranking.TopOffersSelector
import com.top10.groceries.domain.repository.OfferRepository
import com.top10.groceries.domain.usecase.GetTopOffersUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * The one place dependencies are wired. Each platform creates one, passing in what only it
 * knows: [deviceLanguage] is the ISO 639 code of the language the device is set to.
 */
class AppContainer(deviceLanguage: String) {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        // Delhaize takes up to ten seconds to answer one page, and now and then drops one.
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            socketTimeoutMillis = REQUEST_TIMEOUT_MILLIS
        }
        install(HttpRequestRetry) {
            retryOnExceptionOrServerErrors(maxRetries = 2)
            exponentialDelay()
        }
        expectSuccess = true
    }

    private val rankingPolicy = RankingPolicy()

    private val offerRepository: OfferRepository = OfferRepositoryImpl(
        sources = mapOf(
            Store.DELHAIZE to DelhaizeOfferSource(
                api = DelhaizeApi(httpClient),
                mapper = DelhaizeOfferMapper(DelhaizePromotionParser(), DelhaizeCategoryMapper()),
                // Delhaize serves French, Dutch and English.
                language = deviceLanguage.takeIf { it in DELHAIZE_LANGUAGES } ?: "en",
            ),
        ),
    )

    val getTopOffers = GetTopOffersUseCase(
        repository = offerRepository,
        selector = TopOffersSelector(OfferScorer(rankingPolicy), rankingPolicy),
    )

    private companion object {
        const val REQUEST_TIMEOUT_MILLIS = 60_000L
        val DELHAIZE_LANGUAGES = setOf("fr", "nl", "en")
    }
}
