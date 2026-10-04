package com.top10.deals.ads

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.top10.deals.BuildConfig

private const val STORES_BANNER_ID = "ca-app-pub-4164526058911953/8239993143"

/** Google's test banner: debug builds show it so that our own taps never count against the account. */
private const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/9214589741"

/** The AdMob banner under the store list, as wide as the screen. */
@Composable
fun StoresBanner(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val context = LocalContext.current
        val width = maxWidth.value.toInt()
        val adSize = remember(width) { AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width) }
        val adView = remember(adSize) {
            AdView(context).apply {
                adUnitId = if (BuildConfig.DEBUG) TEST_BANNER_ID else STORES_BANNER_ID
                setAdSize(adSize)
                loadAd(AdRequest.Builder().build())
            }
        }
        DisposableEffect(adView) { onDispose { adView.destroy() } }
        LifecycleResumeEffect(adView) {
            adView.resume()
            onPauseOrDispose { adView.pause() }
        }
        // The height is reserved up front so the list does not jump when the ad arrives.
        AndroidView(factory = { adView }, modifier = Modifier.fillMaxWidth().height(adSize.height.dp))
    }
}
