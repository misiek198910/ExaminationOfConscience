package com.example.examinationofconscience.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examinationofconscience.data.billing.SubscriptionManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import kotlinx.coroutines.delay
import pakiet.rachuneksumienia.BuildConfig
import pakiet.rachuneksumienia.R

@Composable
fun SplashScreen(
    subscriptionManager: SubscriptionManager,
    onNavigateToMain: () -> Unit
) {
    val context = LocalContext.current
    val isPremium by subscriptionManager.isPremium.observeAsState(initial = false)

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF000000))
    )

    LaunchedEffect(isPremium) {
        
        var hasNavigated = false

        fun safeNavigateToMain() {
            if (!hasNavigated) {
                hasNavigated = true
                onNavigateToMain()
            }
        }

        if (isPremium) {
            delay(1500)
            safeNavigateToMain()
            return@LaunchedEffect
        }

        var adShown = false
        val adRequest = AdRequest.Builder().build()

        AppOpenAd.load(
            context,
            BuildConfig.AD_ADSTART_ID,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            safeNavigateToMain()
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                            safeNavigateToMain()
                        }
                    }

                    if (!hasNavigated) {
                        ad.show(context as Activity)
                        adShown = true
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    safeNavigateToMain()
                }
            }
        )

        delay(4000)
        if (!adShown) {
            safeNavigateToMain()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.systemBarsPadding()
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = Color(0xFFFFD700),
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.loading_text),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}