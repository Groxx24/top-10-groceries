package com.top10.groceries.ui.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top10.groceries.domain.model.Store
import com.top10.groceries.domain.model.TopOffers
import com.top10.groceries.domain.usecase.GetTopOffersUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TopOffersUiState(
    val store: Store = Store.DELHAIZE,
    val isLoading: Boolean = true,
    /** The last list that loaded. Stays on screen while a refresh runs or after one fails. */
    val top: TopOffers? = null,
    val loadFailed: Boolean = false,
)

class TopOffersViewModel(
    private val getTopOffers: GetTopOffersUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TopOffersUiState())
    val state: StateFlow<TopOffersUiState> = _state.asStateFlow()

    init {
        load(refresh = false)
    }

    fun onRefresh() {
        if (!_state.value.isLoading) load(refresh = true)
    }

    private fun load(refresh: Boolean) {
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        viewModelScope.launch {
            try {
                val top = getTopOffers(_state.value.store, refresh)
                _state.update { it.copy(isLoading = false, top = top) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, loadFailed = true) }
            }
        }
    }
}
