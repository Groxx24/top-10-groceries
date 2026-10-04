package com.top10.deals.ui.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top10.deals.domain.usecase.DeleteEndedTopListsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DeleteEndedStatus {
    data object Idle : DeleteEndedStatus
    data object Deleting : DeleteEndedStatus

    /** Done; [storeNames] are the stores whose list was deleted, empty when none had ended. */
    data class Deleted(val storeNames: List<String>) : DeleteEndedStatus
    data object Failed : DeleteEndedStatus
}

/** Deleting every published top list that has ended. */
class DeleteEndedViewModel(private val deleteEndedTopLists: DeleteEndedTopListsUseCase) : ViewModel() {

    private val _status = MutableStateFlow<DeleteEndedStatus>(DeleteEndedStatus.Idle)
    val status: StateFlow<DeleteEndedStatus> = _status.asStateFlow()

    fun onDelete() {
        if (_status.value == DeleteEndedStatus.Deleting) return
        _status.value = DeleteEndedStatus.Deleting
        viewModelScope.launch {
            _status.value = try {
                DeleteEndedStatus.Deleted(deleteEndedTopLists().map { it.name })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                DeleteEndedStatus.Failed
            }
        }
    }

    /** The result of the last delete has been shown. */
    fun onResultShown() {
        _status.update { if (it == DeleteEndedStatus.Deleting) it else DeleteEndedStatus.Idle }
    }
}
