package com.example.examinationofconscience.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examinationofconscience.data.billing.SubscriptionManager
import com.example.examinationofconscience.ui.components.BannerAdView
import pakiet.rachuneksumienia.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamenCategoriesScreen(
    subscriptionManager: SubscriptionManager,
    typeIndex: Int,
    onBack: () -> Unit,
    onShowSummary: () -> Unit,
    onCategorySelected: (Int, Int) -> Unit
) {
    val arrayRes = if (typeIndex == 0) R.array.rach_data_0 else R.array.rach_data_1
    val categories = stringArrayResource(id = arrayRes)

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF000000))
    )

    val isPremium by subscriptionManager.isPremium.observeAsState(initial = false)

    // Główny kontener na tło Edge-to-Edge
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Scaffold(
            // Zabezpieczenie przed dolnym paskiem nawigacji
            modifier = Modifier.navigationBarsPadding(),
            topBar = {
                CenterAlignedTopAppBar(
                    // Zabezpieczenie przed górnym paskiem (zegar, bateria)
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            text = if (typeIndex == 0) {
                                stringResource(id = R.string.title_10_commandments)
                            } else {
                                stringResource(id = R.string.title_for_spouses)
                            },
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.content_desc_back),
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onShowSummary) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = stringResource(id = R.string.content_desc_summary),
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(categories.toList()) { catIndex, title ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onCategorySelected(typeIndex, catIndex) }
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.05f))
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = title, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
                                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }

                // Reklama tylko dla osób bez Premium, przyklejona do dołu
                if (!isPremium) {
                    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                        BannerAdView(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}