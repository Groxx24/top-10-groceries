package com.top10.products.ui.debug

import com.top10.products.domain.repository.DebugLock
import com.top10.products.domain.usecase.UnlockDebugUseCase
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
class UnlockViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val viewModel = UnlockViewModel(
        UnlockDebugUseCase(
            object : DebugLock {
                override suspend fun opens(passphrase: String) = passphrase == "open sesame"
            },
        ),
    )

    @Test
    fun `a wrong passphrase is reported and does not unlock`() {
        viewModel.onUnlock("guess")

        assertTrue(viewModel.state.value.wrongPassphrase)
        assertFalse(viewModel.state.value.unlocked)
    }

    @Test
    fun `the right passphrase unlocks once, and the next visit asks again`() {
        viewModel.onUnlock("open sesame")
        assertTrue(viewModel.state.value.unlocked)

        viewModel.onUnlockHandled()

        assertEquals(UnlockUiState(), viewModel.state.value)
    }
}
