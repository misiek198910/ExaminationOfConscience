package com.example.examinationofconscience.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.examinationofconscience.data.viewmodel.ExamenViewModel
import com.example.examinationofconscience.ui.components.BannerAdView
import pakiet.rachuneksumienia.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamenListScreen(
    subscriptionManager: SubscriptionManager,
    typeIndex: Int,
    catIndex: Int,
    viewModel: ExamenViewModel,
    onBack: () -> Unit
) {
    val resId = getSinArrayResource(typeIndex, catIndex)
    val sinsArray = if (resId != 0) stringArrayResource(id = resId) else emptyArray()

    // POPRAWKA: Pobieramy nazwę aktualnej kategorii do grupowania w PDF
    val categoryArrayRes = if (typeIndex == 0) R.array.rach_data_0 else R.array.rach_data_1
    val categories = stringArrayResource(id = categoryArrayRes)
    val currentCategoryName = categories.getOrNull(catIndex) ?: ""

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
                            text = stringResource(id = R.string.title_mark_sins),
                            color = Color.White,
                            fontSize = 18.sp,
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(sinsArray) { index, sinText ->
                        val sinId = "${typeIndex}_${catIndex}_$index"
                        val isChecked = viewModel.isChecked(sinId)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isChecked) Color.White.copy(0.2f) else Color.White.copy(0.05f))
                                // POPRAWKA: Przekazujemy currentCategoryName
                                .clickable { viewModel.toggleSin(sinId, sinText, currentCategoryName) }
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isChecked,
                                    // POPRAWKA: Przekazujemy currentCategoryName
                                    onCheckedChange = { viewModel.toggleSin(sinId, sinText, currentCategoryName) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color.White,
                                        uncheckedColor = Color.White.copy(0.5f),
                                        checkmarkColor = Color(0xFF1A237E)
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = sinText,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    // Odstęp dolny, aby reklama nie zasłaniała ostatniego grzechu
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }

                // Reklama tylko dla osób bez Premium, przypięta na samym dole kontenera
                if (!isPremium) {
                    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                        BannerAdView(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

private fun getSinArrayResource(typeIndex: Int, catIndex: Int): Int {
    return when (typeIndex) {
        0 -> when (catIndex) { // Rachunek 10 przykazań
            0 -> R.array.rach_data_items_0_0
            1 -> R.array.rach_data_items_0_1
            2 -> R.array.rach_data_items_0_2
            3 -> R.array.rach_data_items_0_3
            4 -> R.array.rach_data_items_0_4
            5 -> R.array.rach_data_items_0_5
            6 -> R.array.rach_data_items_0_6
            7 -> R.array.rach_data_items_0_7
            else -> 0
        }
        1 -> when (catIndex) { // Rachunek dla małżonków
            0 -> R.array.rach_data_items_1_0
            1 -> R.array.rach_data_items_1_1
            2 -> R.array.rach_data_items_1_2
            3 -> R.array.rach_data_items_1_3
            4 -> R.array.rach_data_items_1_4
            else -> 0
        }
        else -> 0
    }
}