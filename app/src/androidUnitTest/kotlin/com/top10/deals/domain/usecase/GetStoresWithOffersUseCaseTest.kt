package com.top10.deals.domain.usecase

import com.top10.deals.data.hardcoded.HardcodedCatalog
import com.top10.deals.domain.model.Offer
import com.top10.deals.domain.model.TopOffers
import com.top10.deals.domain.repository.TopOffersRepository
import com.top10.deals.fakeDeals
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetStoresWithOffersUseCaseTest {

    private val catalog = HardcodedCatalog()

    private class FakeTopLists(private val all: suspend () -> List<TopOffers>) : TopOffersRepository {
        override suspend fun topOffers(storeId: String) = error("not used")
        override suspend fun allTopOffers() = all()
    }

    @Test
    fun `only the stores with a list are shown, in the catalogue's order`() = runTest {
        val stores = catalog.stores()
        val offers = fakeDeals(10).mapIndexed { index, deal -> Offer(index + 1, deal) }
        val lists = listOf(TopOffers(stores[4], offers), TopOffers(stores[0], offers))

        val shown = GetStoresWithOffersUseCase(catalog, FakeTopLists { lists })()

        assertEquals(listOf(stores[0], stores[4]), shown)
    }

    @Test
    fun `every store is shown when the lists cannot be read`() = runTest {
        val shown = GetStoresWithOffersUseCase(catalog, FakeTopLists { error("offline") })()

        assertEquals(catalog.stores(), shown)
    }
}
