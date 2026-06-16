package com.example.examinationofconscience.ui.components

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import pakiet.rachuneksumienia.BuildConfig

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {

    val adUnitId = BuildConfig.AD_BANNER_ID

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId

                val extras = Bundle().apply {
                    putString("collapsible", "bottom")
                }

                val adRequest = AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                    .build()

                loadAd(adRequest)
            }
        }
    )
}