package com.top10.deals.ads

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation.PrivacyOptionsRequirementStatus
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Asks for consent to ads with Google's form (Belgium is in the EEA, where AdMob requires one),
 * then starts the Mobile Ads SDK. No ad is requested until [canRequestAds] is true.
 */
class AdsConsent(private val activity: Activity) {
    private val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

    var canRequestAds by mutableStateOf(false)
        private set

    /** Whether the user must be offered a way to change their choice, through [showPrivacyOptions]. */
    var privacyOptionsRequired by mutableStateOf(false)
        private set

    /** Checks the consent status, as Google asks on every start, and shows the form if needed. */
    fun gather() {
        consentInformation.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            { UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { update() } },
            { update() },
        )
        // A choice made on an earlier start lets ads load without waiting for the check.
        update()
    }

    fun showPrivacyOptions() {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { update() }
    }

    private fun update() {
        privacyOptionsRequired =
            consentInformation.privacyOptionsRequirementStatus == PrivacyOptionsRequirementStatus.REQUIRED
        if (consentInformation.canRequestAds()) {
            startMobileAds(activity.applicationContext)
            canRequestAds = true
        }
    }

    private companion object {
        val mobileAdsStarted = AtomicBoolean(false)

        /** Once per process, off the main thread as Google recommends. */
        fun startMobileAds(context: Context) {
            if (mobileAdsStarted.getAndSet(true)) return
            Thread { MobileAds.initialize(context) }.start()
        }
    }
}
