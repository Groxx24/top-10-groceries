package com.top10.deals.ui.stores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top10.deals.domain.model.Store
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.TimeMark
import kotlin.time.TimeSource

data class StoresUiState(
    val isLoading: Boolean = true,
    val stores: List<Store> = emptyList(),
    val loadFailed: Boolean = false,
)

/**
 * A list of stores from [loadStores]: those with offers on the store list, every store on the debug
 * screen. Loaded once, then again from [onOpened] when asked to or when it is [maxAge] old.
 */
class StoresViewModel(
    private val loadStores: suspend () -> List<Store>,
    private val maxAge: Duration = MAX_AGE,
    private val timeSource: TimeSource = TimeSource.Monotonic,
) : ViewModel() {

    private val _state = MutableStateFlow(StoresUiState())
    val state: StateFlow<StoresUiState> = _state.asStateFlow()

    private var loading: Job? = null
    private var loadedAt: TimeMark? = null
    private var refreshKey = 0

    init {
        load()
    }

    /**
     * Called each time the screen shows. Loads again when [refreshKey] differs from the last one
     * (the debug screens were left, so a list may have been published) or the stores are [maxAge] old.
     */
    fun onOpened(refreshKey: Int) {
        val asked = refreshKey != this.refreshKey
        this.refreshKey = refreshKey
        val stale = loadedAt?.let { it.elapsedNow() >= maxAge } ?: false
        if (asked || stale) load()
    }

    fun onRetry() = load()

    private fun load() {
        if (loading?.isActive == true) return
        // The stores already shown stay while they are read again.
        _state.update { it.copy(isLoading = it.stores.isEmpty(), loadFailed = false) }
        loading = viewModelScope.launch {
            try {
                val stores = loadStores()
                loadedAt = timeSource.markNow()
                _state.update { it.copy(isLoading = false, stores = stores) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, loadFailed = it.stores.isEmpty()) }
            }
        }
    }

    companion object {
        /** How long the store list is kept before it is read again on opening. */
        val MAX_AGE: Duration = 1.hours
    }
}
