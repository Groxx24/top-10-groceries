package com.top10.deals.domain.usecase

import com.top10.deals.domain.repository.DebugLock

/** Checks the passphrase that opens the debug screens, where top lists are picked and published. */
class UnlockDebugUseCase(private val lock: DebugLock) {
    suspend operator fun invoke(passphrase: String): Boolean = lock.opens(passphrase)
}
