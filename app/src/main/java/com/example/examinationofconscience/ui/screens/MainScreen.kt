package com.example.examinationofconscience.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examinationofconscience.data.billing.SubscriptionManager
import com.example.examinationofconscience.data.viewmodel.NewsViewModel
import pakiet.rachuneksumienia.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    newsViewModel: NewsViewModel,
    subscriptionManager: SubscriptionManager,
    onNavigateToSection: (String) -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val newsList by newsViewModel.newsList
    val lastReadId by newsViewModel.lastReadId

    val hasUpdates = remember(newsList, lastReadId) {
        val latestId = newsList.maxOfOrNull { it.id } ?: 0
        latestId > lastReadId
    }

    LaunchedEffect(Unit) {
        newsViewModel.fetchNews(context)
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A237E), 
            Color(0xFF000000)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(onClick = onNavigateToNews) {
                            BadgedBox(
                                badge = {
                                    if (hasUpdates) {
                                        Badge(
                                            containerColor = Color.Red,
                                            modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = stringResource(id = R.string.cd_news),
                                    tint = Color.White
                                )
                            }
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(id = R.string.cd_settings),
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    GlassMenuCard(
                        title = stringResource(id = R.string.main_section_1_title),
                        subtitle = stringResource(id = R.string.main_section_1_subtitle),
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        onClick = { onNavigateToSection("przygotowanie") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassMenuCard(
                        title = stringResource(id = R.string.main_section_2_title),
                        subtitle = stringResource(id = R.string.main_section_2_subtitle),
                        icon = Icons.Default.Checklist,
                        onClick = { onNavigateToSection("rachunek") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassMenuCard(
                        title = stringResource(id = R.string.main_section_3_title),
                        subtitle = stringResource(id = R.string.main_section_3_subtitle),
                        icon = Icons.Default.CheckCircle,
                        onClick = { onNavigateToSection("po_spowiedzi") }
                    )
                }
            }
        }
    }
}

@Composable
fun GlassMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        }
    }
}