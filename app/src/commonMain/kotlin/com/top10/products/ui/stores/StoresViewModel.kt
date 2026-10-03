package com.top10.products.ui.stores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top10.products.domain.model.Store
import com.top10.products.domain.usecase.GetStoresUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StoresUiState(
    val isLoading: Boolean = true,
    val stores: List<Store> = emptyList(),
    val loadFailed: Boolean = false,
)

class StoresViewModel(
    private val getStores: GetStoresUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(StoresUiState())
    val state: StateFlow<StoresUiState> = _state.asStateFlow()

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
                val stores = getStores()
                _state.update { it.copy(isLoading = false, stores = stores) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, loadFailed = true) }
            }
        }
    }
}
