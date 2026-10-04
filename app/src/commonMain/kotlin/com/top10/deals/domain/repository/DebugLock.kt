package com.top10.deals.domain.repository

interface DebugLock {
    /** True when [passphrase] opens the debug screens. */
    suspend fun opens(passphrase: String): Boolean
}
