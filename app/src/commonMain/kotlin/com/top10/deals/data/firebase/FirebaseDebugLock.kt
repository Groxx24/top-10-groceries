package com.top10.deals.data.firebase

import com.top10.deals.domain.repository.DebugLock
import dev.gitlive.firebase.FirebaseException
import dev.gitlive.firebase.auth.FirebaseAuth

/**
 * Opens when [passphrase] is the password of the publisher's Firebase account, [email], and leaves
 * that account signed in. Firestore's rules allow writes to the top lists only from it, so the
 * lock also holds outside the app.
 */
class FirebaseDebugLock(
    private val auth: FirebaseAuth,
    private val email: String = PUBLISHER_EMAIL,
) : DebugLock {

    override suspend fun opens(passphrase: String): Boolean = try {
        auth.signInWithEmailAndPassword(email, passphrase)
        true
    } catch (e: FirebaseException) {
        // A wrong password, too many tries, or no network: none of them opens.
        false
    }

    companion object {
        /** The account made in the Firebase console; its id is the one `firestore.rules` lets write. */
        const val PUBLISHER_EMAIL = "pavelparradomarin@gmail.com"
    }
}
