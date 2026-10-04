package com.top10.deals.ui.debug

import com.top10.deals.data.hardcoded.HardcodedCatalog
import com.top10.deals.data.hardcoded.HardcodedWeeklyDeals
import com.top10.deals.domain.model.TOP_LIST_SIZE
import com.top10.deals.domain.model.TopOffers
import com.top10.deals.domain.repository.TopListPublisher
import com.top10.deals.domain.usecase.GetWeeklyDealsUseCase
import com.top10.deals.domain.usecase.SubmitTopListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PickTopViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    /** Records what was published, or fails when [fail] is set. */
    private class FakePublisher(private val fail: Boolean = false) : TopListPublisher {
        var published: TopOffers? = null
        override suspend fun publish(top: TopOffers) {
            if (fail) error("offline")
            published = top
        }
    }

    private fun viewModel(publisher: TopListPublisher = FakePublisher()) = PickTopViewModel(
        "delhaize",
        GetWeeklyDealsUseCase(HardcodedCatalog(), HardcodedWeeklyDeals()),
        SubmitTopListUseCase(publisher),
    )

    private val PickTopViewModel.dealIds get() = state.value.candidates!!.deals.map { it.id }

    @Test
    fun `submit is enabled at exactly 10 and an 11th pick is refused`() {
        val viewModel = viewModel()
        val ids = viewModel.dealIds

        ids.take(TOP_LIST_SIZE - 1).forEach(viewModel::onToggle)
        assertFalse(viewModel.state.value.canSubmit)

        viewModel.onToggle(ids[TOP_LIST_SIZE - 1])
        assertTrue(viewModel.state.value.canSubmit)

        viewModel.onToggle(ids[TOP_LIST_SIZE])
        assertEquals(TOP_LIST_SIZE, viewModel.state.value.selectedIds.size)
        assertTrue(viewModel.state.value.canSubmit)
    }

    @Test
    fun `unpicking one of 10 disables submit again`() {
        val viewModel = viewModel()
        val ids = viewModel.dealIds
        ids.take(TOP_LIST_SIZE).forEach(viewModel::onToggle)

        viewModel.onToggle(ids.first())

        assertEquals(TOP_LIST_SIZE - 1, viewModel.state.value.selectedIds.size)
        assertFalse(viewModel.state.value.canSubmit)
    }

    @Test
    fun `submit publishes the 10 picks in the order they are listed`() {
        val publisher = FakePublisher()
        val viewModel = viewModel(publisher)
        val ids = viewModel.dealIds
        ids.take(TOP_LIST_SIZE).reversed().forEach(viewModel::onToggle)

        viewModel.onSubmit()

        val top = publisher.published!!
        assertEquals("delhaize", top.store.id)
        assertEquals(ids.take(TOP_LIST_SIZE), top.offers.map { it.deal.id })
        assertEquals((1..TOP_LIST_SIZE).toList(), top.offers.map { it.rank })
        assertEquals(SubmitStatus.Submitted, viewModel.state.value.submit)
    }

    @Test
    fun `a failed submit is reported and can be retried`() {
        val viewModel = viewModel(FakePublisher(fail = true))
        viewModel.dealIds.take(TOP_LIST_SIZE).forEach(viewModel::onToggle)

        viewModel.onSubmit()

        assertEquals(SubmitStatus.Failed, viewModel.state.value.submit)
        assertTrue(viewModel.state.value.canSubmit)
    }

    @Test
    fun `submit does nothing with fewer than 10`() {
        val publisher = FakePublisher()
        val viewModel = viewModel(publisher)
        viewModel.dealIds.take(TOP_LIST_SIZE - 1).forEach(viewModel::onToggle)

        viewModel.onSubmit()

        assertNull(publisher.published)
        assertEquals(SubmitStatus.Idle, viewModel.state.value.submit)
    }
}
