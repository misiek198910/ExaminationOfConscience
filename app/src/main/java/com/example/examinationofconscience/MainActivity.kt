package com.example.examinationofconscience

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.examinationofconscience.data.billing.SubscriptionManager
import com.example.examinationofconscience.data.viewmodel.ExamenViewModel
import com.example.examinationofconscience.data.viewmodel.NewsViewModel
import com.example.examinationofconscience.ui.screens.AfterConfessionScreen
import com.example.examinationofconscience.ui.screens.ExamenCategoriesScreen
import com.example.examinationofconscience.ui.screens.ExamenListScreen
import com.example.examinationofconscience.ui.screens.ExamenSelectionScreen
import com.example.examinationofconscience.ui.screens.ExamenSummaryScreen
import com.example.examinationofconscience.ui.screens.MainScreen
import com.example.examinationofconscience.ui.screens.NewsScreen
import com.example.examinationofconscience.ui.screens.PreparationScreen
import com.example.examinationofconscience.ui.screens.PrivacyPolicyScreen
import com.example.examinationofconscience.ui.screens.SettingsScreen
import com.example.examinationofconscience.ui.screens.SplashScreen
import com.example.examinationofconscience.ui.screens.SubscriptionScreen
import com.example.examinationofconscience.ui.screens.TextDisplayScreen
import com.example.examinationofconscience.ui.theme.RachunekSumieniaTheme
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val newsViewModel: NewsViewModel by viewModels()
    private val examenViewModel: ExamenViewModel by viewModels()

    private val subscriptionManager by lazy { SubscriptionManager.getInstance(this) }

    private lateinit var appUpdateManager: AppUpdateManager
    private val UPDATE_REQUEST_CODE = 123

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Toast.makeText(this, "Aktualizacja pobrana. Instalowanie...", Toast.LENGTH_LONG).show()
            appUpdateManager.completeUpdate()
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Powiadomienia zostały wyłączone.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = Color.BLACK
            )
        )

        firebaseAnalytics = Firebase.analytics
        MobileAds.initialize(this) {}

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(installStateUpdatedListener)
        checkForAppUpdates()

        askNotificationPermission()
        handleNotificationIntent(intent)

        setContent {
            RachunekSumieniaTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("splash") {
                        SplashScreen(
                            subscriptionManager = subscriptionManager,
                            onNavigateToMain = {
                                navController.navigate("main") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main") {
                        MainScreen(
                            newsViewModel = newsViewModel,
                            subscriptionManager = subscriptionManager,
                            onNavigateToSection = { section -> navController.navigate(section) },
                            onNavigateToNews = { navController.navigate("news") },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }

                    composable("przygotowanie") {
                        PreparationScreen(
                            viewModel = examenViewModel,
                            subscriptionManager = subscriptionManager,
                            onNavigateToDisplay = { navController.navigate("text_display") },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("rachunek") {
                        ExamenSelectionScreen(
                            subscriptionManager = subscriptionManager,
                            onBack = { navController.popBackStack() },
                            onTypeSelected = { typeIndex ->
                                navController.navigate("rachunek_kategorie/$typeIndex")
                            }
                        )
                    }

                    composable("rachunek_kategorie/{typeIndex}") { backStackEntry ->
                        val typeIndex = backStackEntry.arguments?.getString("typeIndex")?.toInt() ?: 0
                        ExamenCategoriesScreen(
                            subscriptionManager = subscriptionManager,
                            typeIndex = typeIndex,
                            onBack = { navController.popBackStack() },
                            onShowSummary = { navController.navigate("podsumowanie") },
                            onCategorySelected = { type, cat ->
                                navController.navigate("rachunek_lista/$type/$cat")
                            }
                        )
                    }

                    composable("rachunek_lista/{typeIndex}/{catIndex}") { backStackEntry ->
                        val typeIndex = backStackEntry.arguments?.getString("typeIndex")?.toInt() ?: 0
                        val catIndex = backStackEntry.arguments?.getString("catIndex")?.toInt() ?: 0
                        ExamenListScreen(
                            typeIndex = typeIndex,
                            catIndex = catIndex,
                            viewModel = examenViewModel,
                            subscriptionManager = subscriptionManager,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("podsumowanie") {
                        ExamenSummaryScreen(
                            viewModel = examenViewModel,
                            subscriptionManager = subscriptionManager,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("po_spowiedzi") {
                        AfterConfessionScreen(
                            viewModel = examenViewModel,
                            subscriptionManager = subscriptionManager,
                            onNavigateToDisplay = { navController.navigate("text_display") },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("text_display") {
                        TextDisplayScreen(
                            viewModel = examenViewModel,
                            subscriptionManager = subscriptionManager,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("news") {
                        NewsScreen(
                            viewModel = newsViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            onNavigateToSubscription = { navController.navigate("subscription") },
                            onNavigateToPrivacyPolicy = { navController.navigate("privacy_policy") },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("subscription") {
                        SubscriptionScreen(
                            subscriptionManager = subscriptionManager,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("privacy_policy") {
                        PrivacyPolicyScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }
    private fun handleNotificationIntent(intent: Intent?) {
        intent?.extras?.let { extras ->
            val action = extras.getString("action")
            val packageName = extras.getString("packageName")

            if (action == "ToStore" && !packageName.isNullOrEmpty()) {
                toStore(this, packageName)
            }
        }
    }
    private fun toStore(context: Context, packageName: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW,
                "market://details?id=$packageName".toUri()))
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$packageName".toUri()))
        }
    }
    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)

            if (isUpdateAvailable && isUpdateAllowed) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        AppUpdateType.FLEXIBLE,
                        this,
                        UPDATE_REQUEST_CODE
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }
}