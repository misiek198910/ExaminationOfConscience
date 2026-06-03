package com.example.examinationofconscience.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import pakiet.rachuneksumienia.BuildConfig

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    // 1. Pobieramy konfigurację urządzenia, aby poznać szerokość ekranu
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                // 2. Ustawiamy Twoje ID reklamy
                adUnitId = BuildConfig.AD_BANNER_ID

                // 3. Prosimy Google o wygenerowanie rozmiaru adaptacyjnego dla tej szerokości
                val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context,
                    screenWidth
                )
                setAdSize(adSize)

                // 4. Ładujemy reklamę z nowym, idealnie dopasowanym rozmiarem
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}