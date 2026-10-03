package com.top10.products.ui.debug

import com.top10.products.data.hardcoded.HardcodedCatalog
import com.top10.products.data.hardcoded.HardcodedWeeklyDeals
import com.top10.products.domain.model.TOP_LIST_SIZE
import com.top10.products.domain.usecase.GetWeeklyDealsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PickTopViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() =
        PickTopViewModel("delhaize", GetWeeklyDealsUseCase(HardcodedCatalog(), HardcodedWeeklyDeals()))

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
}
