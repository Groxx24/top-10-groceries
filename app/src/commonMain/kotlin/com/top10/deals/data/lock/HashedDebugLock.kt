package com.top10.deals.data.lock

import com.top10.deals.domain.repository.DebugLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256

/**
 * Opens with the passphrase whose PBKDF2-SHA256 hash is [hashHex]. The app holds only the salt and
 * the hash, so the passphrase cannot be read out of it, and the many [iterations] make guessing it
 * offline slow.
 */
class HashedDebugLock(
    private val saltHex: String = DebugPassphrase.SALT_HEX,
    private val hashHex: String = DebugPassphrase.HASH_HEX,
    private val iterations: Int = DebugPassphrase.ITERATIONS,
) : DebugLock {

    override suspend fun opens(passphrase: String): Boolean = withContext(Dispatchers.Default) {
        // HMAC refuses an empty key, and no passphrase is empty anyway.
        if (passphrase.isEmpty()) return@withContext false
        val expected = hashHex.hexToByteArray()
        val actual = pbkdf2Sha256(passphrase.encodeToByteArray(), saltHex.hexToByteArray(), iterations)
        constantTimeEquals(actual, expected)
    }
}

/** PBKDF2 with HMAC-SHA256 and a 32-byte key, which is a single block (RFC 8018). */
internal fun pbkdf2Sha256(password: ByteArray, salt: ByteArray, iterations: Int): ByteArray {
    val hmac = HmacSHA256(password)
    var u = hmac.doFinal(salt + byteArrayOf(0, 0, 0, 1))
    val result = u.copyOf()
    repeat(iterations - 1) {
        u = hmac.doFinal(u)
        for (i in result.indices) result[i] = (result[i].toInt() xor u[i].toInt()).toByte()
    }
    return result
}

/** Compares every byte, so how long it takes says nothing about where they differ. */
private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
    if (a.size != b.size) return false
    var difference = 0
    for (i in a.indices) difference = difference or (a[i].toInt() xor b[i].toInt())
    return difference == 0
}
