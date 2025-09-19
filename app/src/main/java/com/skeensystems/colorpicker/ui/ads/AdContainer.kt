package com.skeensystems.colorpicker.ui.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.skeensystems.colorpicker.BuildConfig

@Composable
fun AdContainer(width: Dp) {
    val context = LocalContext.current
    val adView = remember { AdView(context) }
    adView.adUnitId = BuildConfig.adUnitID

    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(LocalContext.current, width.value.toInt())
    adView.setAdSize(adSize)

    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)

    Box(modifier = Modifier.fillMaxWidth()) { BannerAd(adView, Modifier) }
}
