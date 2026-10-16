package com.top10.deals.domain.usecase

import com.top10.deals.domain.model.Store
import com.top10.deals.domain.repository.StoreRepository
import com.top10.deals.domain.repository.TopOffersRepository
import kotlinx.coroutines.CancellationException

/**
 * The stores worth opening: those with a top list this week, in the catalogue's order. When the
 * lists cannot be read at all, every store, so the app is never left empty for want of a network.
 */
class GetStoresWithOffersUseCase(
    private val stores: StoreRepository,
    private val topLists: TopOffersRepository,
) {
    suspend operator fun invoke(): List<Store> {
        val all = stores.stores()
        val withOffers = try {
            topLists.allTopOffers().map { it.store.id }.toSet()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return all
        }
        return all.filter { it.id in withOffers }
    }
}
