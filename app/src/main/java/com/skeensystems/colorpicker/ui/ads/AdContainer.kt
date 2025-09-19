package com.skeensystems.colorpicker.ui.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.skeensystems.colorpicker.BuildConfig
import kotlinx.coroutines.launch

@Composable
fun AdContainer(width: Dp) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val adView = remember { AdView(context) }
    adView.adUnitId = BuildConfig.adUnitID

    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(LocalContext.current, width.value.toInt())
    adView.setAdSize(adSize)

    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)

    adView.adListener =
        object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                scope.launch {
                    collectAdsData("l")
                }
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                scope.launch {
                    collectAdsData(p0.toString())
                }
            }

            override fun onAdClicked() {
                super.onAdClicked()
                scope.launch {
                    collectAdsData("c")
                }
            }

            override fun onAdImpression() {
                super.onAdImpression()
                scope.launch {
                    collectAdsData("i")
                }
            }
        }

    Box(modifier = Modifier.fillMaxWidth()) { BannerAd(adView, Modifier) }
}
