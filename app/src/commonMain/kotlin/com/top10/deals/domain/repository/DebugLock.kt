package com.top10.deals.domain.repository

interface DebugLock {
    /** True when [passphrase] opens the debug screens, and lets them publish top lists. */
    suspend fun opens(passphrase: String): Boolean
}
