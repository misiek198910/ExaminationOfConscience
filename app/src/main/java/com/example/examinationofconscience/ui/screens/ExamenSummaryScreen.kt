package com.example.examinationofconscience.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.examinationofconscience.data.billing.SubscriptionManager
import com.example.examinationofconscience.data.viewmodel.ExamenViewModel
import com.example.examinationofconscience.data.viewmodel.SinItem
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import pakiet.rachuneksumienia.BuildConfig
import pakiet.rachuneksumienia.R
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamenSummaryScreen(
    viewModel: ExamenViewModel,
    subscriptionManager: SubscriptionManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val checkedSins = viewModel.checkedSins
    var showDeleteDialog by remember { mutableStateOf(false) }
    val activity = context as? Activity
    val isPremium by subscriptionManager.isPremium.observeAsState(initial = false)

    // Logika reklamy pełnoekranowej
    LaunchedEffect(Unit) {
        if (!isPremium) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                BuildConfig.AD_INTERSTITIAL_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        if (activity != null) {
                            interstitialAd.show(activity)
                        }
                    }
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.e("Ads", "Błąd ładowania reklamy: ${adError.message}")
                    }
                }
            )
        }
    }

    // Launcher do zapisu pliku PDF wybranego przez użytkownika
    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
        onResult = { uri ->
            // POPRAWKA: Przekazujemy całą listę checkedSins, a nie tylko mapowane nazwy
            uri?.let { generatePdf(context, it, checkedSins) }
        }
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF000000))
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(id = R.string.dialog_clear_list_title)) },
            text = { Text(stringResource(id = R.string.dialog_clear_list_text)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAll()
                    showDeleteDialog = false
                }) { Text(stringResource(id = R.string.dialog_clear_list_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(id = R.string.dialog_clear_list_cancel)) }
            }
        )
    }

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
                    title = { Text(stringResource(id = R.string.title_summary), color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.content_desc_back), tint = Color.White)
                        }
                    },
                    actions = {
                        if (checkedSins.isNotEmpty()) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = stringResource(id = R.string.content_desc_clear_all), tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (checkedSins.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.empty_sins_list),
                            color = Color.White.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .padding(32.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(checkedSins) { sin ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "• ${sin.name}",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = { viewModel.toggleSin(sin.id, sin.name, sin.category) }) {
                                            Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.content_desc_delete), tint = Color.White.copy(alpha = 0.6f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (checkedSins.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SummaryActionItem(Icons.Default.ContentCopy, stringResource(id = R.string.action_copy)) {
                                val textToCopy = checkedSins.joinToString("\n") { "• ${it.name}" }
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Grzechy", textToCopy)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, context.getString(R.string.toast_copied), Toast.LENGTH_SHORT).show()
                            }
                            SummaryActionItem(Icons.Default.PictureAsPdf, stringResource(id = R.string.action_save)) {
                                createPdfLauncher.launch("Rachunek_Sumienia.pdf")
                            }
                            SummaryActionItem(Icons.Default.Share, stringResource(id = R.string.action_send)) {
                                // POPRAWKA: Przekazujemy całą listę checkedSins
                                sharePdf(context, checkedSins)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        Text(text = label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

// WYDZIELONA FUNKCJA: Tworzy i formatuje obiekt PdfDocument (używana przez Zapisz i Udostępnij)
private fun createPdfContent(context: Context, sins: List<SinItem>): PdfDocument {
    val pdfDocument = PdfDocument()
    val titlePaint = Paint().apply { isFakeBoldText = true; textSize = 20f; color = android.graphics.Color.BLACK }
    val categoryPaint = Paint().apply { isFakeBoldText = true; textSize = 16f; color = android.graphics.Color.DKGRAY }
    val textPaint = Paint().apply { textSize = 14f; color = android.graphics.Color.BLACK }
    val datePaint = Paint().apply { textSize = 10f; color = android.graphics.Color.GRAY }

    val pageHeight = 842
    val pageWidth = 595
    val margin = 50f
    var currentPageNumber = 1

    var currentPage: PdfDocument.Page? = null
    var canvas: Canvas? = null
    var y = margin + 20f

    fun startNewPage() {
        currentPage?.let { pdfDocument.finishPage(it) }
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber++).create()
        currentPage = pdfDocument.startPage(pageInfo)
        canvas = currentPage?.canvas
        y = margin + 20f
    }

    startNewPage()

    canvas?.drawText(context.getString(R.string.pdf_title), margin, y, titlePaint)
    y += 20f
    val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    canvas?.drawText(context.getString(R.string.pdf_date_prefix, currentDateTime), margin, y, datePaint)
    y += 40f

    val groupedSins = sins.groupBy { it.category }

    groupedSins.forEach { (category, sinsInCategory) ->
        if (y > pageHeight - margin - 40f) {
            startNewPage()
        }

        canvas?.drawText(category, margin, y, categoryPaint)
        y += 25f

        sinsInCategory.forEach { sin ->
            val words = "• ${sin.name}".split(" ")
            var line = ""

            words.forEach { word ->
                if (textPaint.measureText("$line$word ") < (pageWidth - 2 * margin)) {
                    line += "$word "
                } else {
                    canvas?.drawText(line, margin + 10f, y, textPaint)
                    y += 20f
                    line = "  $word "

                    if (y > pageHeight - margin) {
                        startNewPage()
                    }
                }
            }
            canvas?.drawText(line, margin + 10f, y, textPaint)
            y += 30f

            if (y > pageHeight - margin) {
                startNewPage()
            }
        }
        y += 10f
    }

    currentPage?.let { pdfDocument.finishPage(it) }

    return pdfDocument
}

private fun generatePdf(context: Context, uri: Uri, sins: List<SinItem>) {
    val pdfDocument = createPdfContent(context, sins)

    try {
        context.contentResolver.openOutputStream(uri)?.use { pdfDocument.writeTo(it) }
        Toast.makeText(context, context.getString(R.string.toast_pdf_saved), Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.toast_pdf_error), Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
}
private fun sharePdf(context: Context, sins: List<SinItem>) {
    val fileName = "RachunekSumienia.pdf"
    val file = File(context.cacheDir, fileName)
    val pdfDocument = createPdfContent(context, sins)

    try {
        pdfDocument.writeTo(FileOutputStream(file))
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.toast_pdf_error), Toast.LENGTH_SHORT).show()
        pdfDocument.close()
        return
    }

    pdfDocument.close()

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_chooser_title)))
}