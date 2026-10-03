package com.top10.products.data.lock

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HashedDebugLockTest {

    @Test
    fun `pbkdf2 matches the published test vectors`() {
        // RFC 7914, section 11, and the widely used PBKDF2-HMAC-SHA256 vector with 4096 rounds.
        assertEquals(
            "55ac046e56e3089fec1691c22544b605f94185216dde0465e68b9d57c20dacbc",
            pbkdf2Sha256("passwd".encodeToByteArray(), "salt".encodeToByteArray(), 1).toHexString(),
        )
        assertEquals(
            "c5e478d59288c841aa530db6845c4c8d962893a001ce4e11a4963873aa98134a",
            pbkdf2Sha256("password".encodeToByteArray(), "salt".encodeToByteArray(), 4096).toHexString(),
        )
    }

    // A test passphrase, hashed the way scripts/set-debug-passphrase.py does; not the app's one.
    private val lock = HashedDebugLock(
        saltHex = "00112233445566778899aabbccddeeff",
        hashHex = "4c5805526868c915a4cba43d75ec6b0a07152bb7b9ca30501c96ea0f91886829",
        iterations = 1000,
    )

    @Test
    fun `only the right passphrase opens the lock`() = runTest {
        assertTrue(lock.opens("correct horse"))
        assertFalse(lock.opens("correct horse "))
        assertFalse(lock.opens("Correct horse"))
        assertFalse(lock.opens(""))
    }

    @Test
    fun `the app's lock refuses a guess`() = runTest {
        assertFalse(HashedDebugLock().opens("debug"))
    }
}
