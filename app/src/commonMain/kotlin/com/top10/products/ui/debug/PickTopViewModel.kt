package com.top10.products.ui.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top10.products.domain.model.TOP_LIST_SIZE
import com.top10.products.domain.model.WeeklyDeals
import com.top10.products.domain.usecase.GetWeeklyDealsUseCase
import com.top10.products.domain.usecase.SubmitTopListUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PickTopUiState(
    val isLoading: Boolean = true,
    val candidates: WeeklyDeals? = null,
    val loadFailed: Boolean = false,
    /** Ids of the picked deals. Never more than [TOP_LIST_SIZE]. */
    val selectedIds: Set<String> = emptySet(),
    val submit: SubmitStatus = SubmitStatus.Idle,
) {
    val isFull: Boolean get() = selectedIds.size >= TOP_LIST_SIZE

    /** A top list has exactly [TOP_LIST_SIZE] products, no fewer and no more. */
    val canSubmit: Boolean get() = selectedIds.size == TOP_LIST_SIZE && submit != SubmitStatus.Submitting
}

enum class SubmitStatus { Idle, Submitting, Submitted, Failed }

/** Picking the top list of the store with [storeId] from its weekly deals. */
class PickTopViewModel(
    private val storeId: String,
    private val getWeeklyDeals: GetWeeklyDealsUseCase,
    private val submitTopList: SubmitTopListUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PickTopUiState())
    val state: StateFlow<PickTopUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun onRetry() {
        if (!_state.value.isLoading) load()
    }

    /** Picks or unpicks a deal. Once [TOP_LIST_SIZE] are picked, another one has to be unpicked first. */
    fun onToggle(dealId: String) {
        _state.update { state ->
            when {
                dealId in state.selectedIds -> state.copy(selectedIds = state.selectedIds - dealId)
                state.isFull -> state
                else -> state.copy(selectedIds = state.selectedIds + dealId)
            }
        }
    }

    /** Publishes the picked deals as the store's top list, in the order the candidates are listed. */
    fun onSubmit() {
        val state = _state.value
        val candidates = state.candidates ?: return
        if (!state.canSubmit) return
        val picked = candidates.deals.filter { it.id in state.selectedIds }
        _state.update { it.copy(submit = SubmitStatus.Submitting) }
        viewModelScope.launch {
            val status = try {
                submitTopList(candidates.store, picked)
                SubmitStatus.Submitted
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                SubmitStatus.Failed
            }
            _state.update { it.copy(submit = status) }
        }
    }

    /** The result of the last submit has been shown. */
    fun onSubmitResultShown() {
        _state.update { if (it.submit == SubmitStatus.Submitting) it else it.copy(submit = SubmitStatus.Idle) }
    }

    private fun load() {
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        viewModelScope.launch {
            try {
                val candidates = getWeeklyDeals(storeId)
                _state.update { it.copy(isLoading = false, candidates = candidates) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, loadFailed = true) }
            }
        }
    }
}
