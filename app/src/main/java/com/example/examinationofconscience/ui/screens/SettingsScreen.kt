package com.example.examinationofconscience.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examinationofconscience.ui.components.AppInfoDialog
import pakiet.rachuneksumienia.BuildConfig
import pakiet.rachuneksumienia.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToSubscription: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onBack: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF1A237E), Color(0xFF000000)))
    var showInfoDialog by remember { mutableStateOf(false) }
    val theme = MaterialTheme.colorScheme

    val appVersion = BuildConfig.VERSION_NAME

    // Inicjalizacja handlera do otwierania linków w przeglądarce
    val uriHandler = LocalUriHandler.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            text = stringResource(R.string.settings_title),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.content_desc_back),
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {

                    Text(
                        text = stringResource(R.string.settings_category_app),
                        color = Color.White.copy(0.5f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(8.dp)
                    )

                    Box(modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(Color.White.copy(0.1f))) {
                        Column {
                            SettingsRow(
                                icon = Icons.Default.WorkspacePremium,
                                title = stringResource(R.string.settings_premium_title),
                                subtitle = stringResource(R.string.settings_premium_subtitle),
                                iconColor = Color(0xFFFFD700),
                                onClick = onNavigateToSubscription
                            )
                            HorizontalDivider(color = Color.White.copy(0.05f))
                            SettingsRow(
                                icon = Icons.Default.PrivacyTip,
                                title = stringResource(R.string.settings_privacy_title),
                                subtitle = stringResource(R.string.settings_privacy_subtitle),
                                onClick = onNavigateToPrivacyPolicy
                            )
                            HorizontalDivider(color = Color.White.copy(0.05f))
                            SettingsRow(
                                icon = Icons.Default.Info,
                                title = stringResource(R.string.settings_about_title),
                                subtitle = stringResource(R.string.settings_version_subtitle, appVersion),
                                onClick = { showInfoDialog = true }
                            )
                            HorizontalDivider(color = Color.White.copy(0.05f))
                            // Nowa zakładka: Postaw kawę
                            SettingsRow(
                                icon = Icons.Default.Coffee,
                                title = stringResource(R.string.settings_coffee),
                                subtitle = stringResource(R.string.settings_coffee_subtitle),
                                onClick = { uriHandler.openUri("https://ko-fi.com/michals") }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    if (showInfoDialog) {
        AppInfoDialog(
            title = stringResource(R.string.settings_about_title),
            message = stringResource(R.string.about_app_description),
            onDismiss = { showInfoDialog = false },
            primaryColor = theme.primary
        )
    }
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(0.6f), fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(0.3f))
    }
}