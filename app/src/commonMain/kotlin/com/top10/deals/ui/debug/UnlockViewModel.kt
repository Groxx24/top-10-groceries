package com.top10.deals.ui.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.top10.deals.domain.usecase.UnlockDebugUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UnlockUiState(
    val isChecking: Boolean = false,
    val wrongPassphrase: Boolean = false,
    /** Set once the right passphrase was entered, until the screen has moved on. */
    val unlocked: Boolean = false,
)

/** The passphrase that opens the debug screens. */
class UnlockViewModel(private val unlockDebug: UnlockDebugUseCase) : ViewModel() {

    private val _state = MutableStateFlow(UnlockUiState())
    val state: StateFlow<UnlockUiState> = _state.asStateFlow()

    fun onUnlock(passphrase: String) {
        if (_state.value.isChecking || passphrase.isEmpty()) return
        _state.update { it.copy(isChecking = true, wrongPassphrase = false) }
        viewModelScope.launch {
            val opens = unlockDebug(passphrase)
            _state.update { it.copy(isChecking = false, wrongPassphrase = !opens, unlocked = opens) }
        }
    }

    /**
     * The screen has moved on. This view model outlives the screen, so without this the next visit
     * would find it still unlocked and skip the passphrase.
     */
    fun onUnlockHandled() {
        _state.value = UnlockUiState()
    }
}
