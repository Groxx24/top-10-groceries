package com.top10.deals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.top10.deals.ads.AdsConsent
import com.top10.deals.ads.StoresBanner
import com.top10.deals.ui.App

class MainActivity : ComponentActivity() {
    private val container get() = (application as Top10DealsApplication).container

    // Lazy because the activity has no context yet while its fields are set.
    private val adsConsent by lazy { AdsConsent(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        adsConsent.gather()
        setContent {
            App(
                container,
                isDebugBuild = BuildConfig.DEBUG,
                storesBanner = { if (adsConsent.canRequestAds) StoresBanner() },
                onOpenPrivacyOptions = if (adsConsent.privacyOptionsRequired) adsConsent::showPrivacyOptions else null,
            )
        }
    }
}
