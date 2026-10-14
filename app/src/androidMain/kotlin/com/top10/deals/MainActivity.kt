package com.top10.deals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.top10.deals.ads.AdsConsent
import com.top10.deals.ads.DealsBanner
import com.top10.deals.ads.StoresBanner
import com.top10.deals.di.DebugContainer
import com.top10.deals.ui.App
import com.top10.deals.ui.debug.DebugScreens

class MainActivity : ComponentActivity() {
    private val container get() = (application as Top10DealsApplication).container

    // Lazy because the activity has no context yet while its fields are set.
    private val adsConsent by lazy { AdsConsent(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        adsConsent.gather()
        // Only a debug build gets the screens that publish the top lists. BuildConfig.DEBUG is a
        // constant, so in a release build R8 sees this is always null and drops those screens,
        // their wiring and the publisher login from the app.
        val debug = if (BuildConfig.DEBUG) DebugContainer(container) else null
        setContent {
            App(
                container,
                debugScreens = debug?.let { { onClose -> DebugScreens(it, onClose) } },
                storesBanner = { if (adsConsent.canRequestAds) StoresBanner() },
                dealsBanner = { if (adsConsent.canRequestAds) DealsBanner() },
                onOpenPrivacyOptions = if (adsConsent.privacyOptionsRequired) adsConsent::showPrivacyOptions else null,
            )
        }
    }
}
