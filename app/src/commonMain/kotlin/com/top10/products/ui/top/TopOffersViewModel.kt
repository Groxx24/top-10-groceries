package com.top10.products.ui.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top10.products.domain.model.TopOffers
import com.top10.products.domain.usecase.GetTopOffersUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TopOffersUiState(
    val isLoading: Boolean = true,
    val top: TopOffers? = null,
    val loadFailed: Boolean = false,
)

/** The top list of the store with [storeId]. */
class TopOffersViewModel(
    private val storeId: String,
    private val getTopOffers: GetTopOffersUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TopOffersUiState())
    val state: StateFlow<TopOffersUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun onRetry() {
        if (!_state.value.isLoading) load()
    }

    private fun load() {
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        viewModelScope.launch {
            try {
                val top = getTopOffers(storeId)
                _state.update { it.copy(isLoading = false, top = top) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, loadFailed = true) }
            }
        }
    }
}
