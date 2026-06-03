import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace = "pakiet.rachuneksumienia"
    compileSdk = 36

    defaultConfig {
        manifestPlaceholders += mapOf()
        applicationId = "pakiet.rachuneksumienia"
        minSdk = 26
        targetSdk = 36
        versionCode = 28
        versionName = "2.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["adMobAppId"] = "ca-app-pub-3940256099942544~3347511713"
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties.getProperty("MYAPP_RELEASE_STORE_FILE") ?: "debug.keystore")
            storePassword = localProperties.getProperty("MYAPP_RELEASE_STORE_PASSWORD") ?: ""
            keyAlias = localProperties.getProperty("MYAPP_RELEASE_KEY_ALIAS") ?: ""
            keyPassword = localProperties.getProperty("MYAPP_RELEASE_KEY_PASSWORD") ?: ""
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders["adMobAppId"] = "ca-app-pub-3940256099942544~3347511713"

            buildConfigField("String", "AD_BANNER_ID", "\"ca-app-pub-3940256099942544/6300978111\"")
            buildConfigField("String", "AD_INTERSTITIAL_ID", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "AD_ADSTART_ID", "\"ca-app-pub-3940256099942544/9257395921\"")
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            val adAppId = localProperties.getProperty("AD_APP_ID") ?: ""
            val bannerId = localProperties.getProperty("AD_BANNER_ID") ?: ""
            val interstitialId = localProperties.getProperty("AD_INTERSTITIAL_ID") ?: ""
            val adStartId = localProperties.getProperty("AD_ADSTART_ID") ?: ""

            manifestPlaceholders["adMobAppId"] = adAppId
            buildConfigField("String", "AD_BANNER_ID", "\"$bannerId\"")
            buildConfigField("String", "AD_INTERSTITIAL_ID", "\"$interstitialId\"")
            buildConfigField("String", "AD_ADSTART_ID", "\"$adStartId\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Core
    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    // Ads & UMP
    implementation(libs.google.ads)
    implementation(libs.google.ump)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.app.update.ktx)

    // Tooling
    debugImplementation(libs.androidx.ui.tooling)

    //Ikony
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.bundles.network)
    implementation(libs.firebase.messaging.ktx)

    // Biblioteka GSON
    implementation(libs.gson)

    // Biblioteka Coil do obrazków w NewsScreen
    implementation(libs.coil.compose)
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    implementation(libs.billing.ktx)


    // Testy
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}