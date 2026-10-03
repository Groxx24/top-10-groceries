package com.top10.products.data.lock

/**
 * The debug passphrase, hashed. Written by `scripts/set-debug-passphrase.py`; run it to change the
 * passphrase instead of editing this file. Only a salt and a PBKDF2-SHA256 hash are kept, so the
 * passphrase cannot be read back from the source or the app.
 */
internal object DebugPassphrase {
    const val ITERATIONS = 100000
    const val SALT_HEX = "a1689989a69dce5f5992530bb9b2c89e"
    const val HASH_HEX = "23234f20bee59d2a1d1c65843660dbaa24a96c92ed65d00c9e635ae4d65290ef"
}
