package com.top10.products.ui.top

import com.top10.products.data.hardcoded.HardcodedCatalog
import com.top10.products.data.hardcoded.HardcodedWeeklyDeals
import com.top10.products.domain.model.Offer
import com.top10.products.domain.model.TopOffers
import com.top10.products.domain.repository.TopOffersRepository
import com.top10.products.domain.usecase.GetTopOffersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TopOffersViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val catalog = HardcodedCatalog()

    /** Aldi's list, empty until [published] is set. */
    private inner class FakeTopLists : TopOffersRepository {
        var published: List<Offer> = emptyList()
        override suspend fun topOffers(storeId: String) =
            TopOffers(runBlocking { catalog.stores() }.first { it.id == storeId }, published)
    }

    @Test
    fun `opening the store again shows a list published since`() {
        val topLists = FakeTopLists()
        val viewModel = TopOffersViewModel("aldi", GetTopOffersUseCase(topLists))

        viewModel.onOpened()
        assertEquals(emptyList<Offer>(), viewModel.state.value.top?.offers)

        val deals = runBlocking { HardcodedWeeklyDeals().weeklyDeals("aldi") }
        topLists.published = deals.take(10).mapIndexed { index, deal -> Offer(index + 1, deal) }
        viewModel.onOpened()

        assertEquals(10, viewModel.state.value.top?.offers?.size)
    }
}
