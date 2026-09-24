package com.example.scanify.presentation.home

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanify.R
import com.example.scanify.core.utils.DateUtils
import com.example.scanify.data.scanner.BarcodeMapper
import com.example.scanify.domain.model.GeneratedQR
import com.example.scanify.domain.model.ScanResult
import com.example.scanify.ui.theme.CategoryContact
import com.example.scanify.ui.theme.CategoryContactContainer
import com.example.scanify.ui.theme.CategoryEmail
import com.example.scanify.ui.theme.CategoryEmailContainer
import com.example.scanify.ui.theme.CategoryGeo
import com.example.scanify.ui.theme.CategoryGeoContainer
import com.example.scanify.ui.theme.CategoryPhone
import com.example.scanify.ui.theme.CategoryPhoneContainer
import com.example.scanify.ui.theme.CategorySms
import com.example.scanify.ui.theme.CategorySmsContainer
import com.example.scanify.ui.theme.CategoryText
import com.example.scanify.ui.theme.CategoryTextContainer
import com.example.scanify.ui.theme.CategoryUrl
import com.example.scanify.ui.theme.CategoryUrlContainer
import com.example.scanify.ui.theme.CategoryWifi
import com.example.scanify.ui.theme.CategoryWifiContainer
import com.example.scanify.ui.theme.HeroScanGradientDark
import com.example.scanify.ui.theme.HeroScanGradientLight
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToScanner: () -> Unit,
    onNavigateToGenerator: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
    onNavigateToSettings: () -> Unit,
    onNavigateToResult: (ScanResult) -> Unit,
    onNavigateToGeneratedDetail: (GeneratedQR) -> Unit
) {
    val context = LocalContext.current
    val recentActivity by viewModel.recentActivity.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    var showNoQrDialog by remember { mutableStateOf(false) }
    var isProcessingGallery by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadRecentActivity()
    }

    val processGalleryUri: (Uri) -> Unit = { uri ->
        isProcessingGallery = true
        try {
            val image = InputImage.fromFilePath(context, uri)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_AZTEC,
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_EAN_8,
                    Barcode.FORMAT_DATA_MATRIX,
                    Barcode.FORMAT_PDF417,
                    Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E,
                    Barcode.FORMAT_CODE_128,
                    Barcode.FORMAT_CODE_93,
                    Barcode.FORMAT_CODE_39
                )
                .build()

            val scanner = BarcodeScanning.getClient(options)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    isProcessingGallery = false
                    if (barcodes.isNotEmpty()) {
                        val firstBarcode = barcodes.first()
                        val rawValue = BarcodeMapper.extractValue(firstBarcode)
                        val formatName = BarcodeMapper.getFormatName(firstBarcode.format)
                        val typeName = BarcodeMapper.formatType(firstBarcode)
                        val scanResult = ScanResult(
                            id = 0L,
                            rawValue = rawValue,
                            format = formatName,
                            scanType = typeName,
                            timestamp = System.currentTimeMillis()
                        )
                        onNavigateToResult(scanResult)
                    } else {
                        showNoQrDialog = true
                    }
                }
                .addOnFailureListener { e ->
                    isProcessingGallery = false
                    Toast.makeText(context, "Failed to scan image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (_: Exception) {
            isProcessingGallery = false
            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) processGalleryUri(uri)
    }

    val galleryFallback = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) processGalleryUri(uri)
    }

    val launchGallery = {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            galleryFallback.launch("image/*")
        }
    }

    if (showNoQrDialog) {
        AlertDialog(
            onDismissRequest = { showNoQrDialog = false },
            title = {
                Text(
                    text = "No Code Detected",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "The selected image does not contain a readable QR code or barcode. Make sure the code is clear and well-lit.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showNoQrDialog = false }) {
                    Text(text = "OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToScanner,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.QrCodeScanner,
                        contentDescription = stringResource(id = R.string.scan),
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.scan),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier.padding(bottom = navBarBottom + 8.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = statusBarTop + 8.dp,
                    bottom = navBarBottom + 96.dp
                )
            ) {
                // Header with Greeting and Date
                item {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val greeting = when {
                        hour < 12 -> stringResource(id = R.string.greeting_morning)
                        hour < 17 -> stringResource(id = R.string.greeting_afternoon)
                        else -> stringResource(id = R.string.greeting_evening)
                    }
                    val formattedDate = remember {
                        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh
                            ) {
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = greeting,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = stringResource(id = R.string.settings),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Expressive Hero Scanner Banner
                item {
                    val isDark = MaterialTheme.colorScheme.surface.red < 0.5f
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                            .clickable { onNavigateToScanner() },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isDark) HeroScanGradientDark else HeroScanGradientLight)
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = Color.White.copy(alpha = 0.2f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.AutoAwesome,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "AI-Powered",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "Instant Scanner",
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Scan QR codes & barcodes instantly with camera preview",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.85f),
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Button(
                                        onClick = onNavigateToScanner,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = MaterialTheme.colorScheme.primary
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.QrCodeScanner,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(id = R.string.start_scanning),
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.QrCodeScanner,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(44.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Actions Section Title
                item {
                    Text(
                        text = stringResource(id = R.string.quick_actions),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 10.dp)
                    )
                }

                // Modern 2x2 Quick Actions
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ModernQuickActionCard(
                                icon = Icons.Rounded.QrCodeScanner,
                                title = stringResource(id = R.string.scan),
                                subtitle = "Live Camera",
                                iconTint = MaterialTheme.colorScheme.primary,
                                iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToScanner
                            )
                            ModernQuickActionCard(
                                icon = Icons.Rounded.QrCode,
                                title = stringResource(id = R.string.generate_qr_title),
                                subtitle = "Custom Styles",
                                iconTint = MaterialTheme.colorScheme.secondary,
                                iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToGenerator
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ModernQuickActionCard(
                                icon = Icons.Rounded.Image,
                                title = stringResource(id = R.string.scan_from_gallery),
                                subtitle = "Import Image",
                                iconTint = CategoryUrl,
                                iconBgColor = CategoryUrlContainer,
                                modifier = Modifier.weight(1f),
                                onClick = launchGallery
                            )
                            ModernQuickActionCard(
                                icon = Icons.Rounded.History,
                                title = stringResource(id = R.string.history),
                                subtitle = "Past Scans",
                                iconTint = CategoryContact,
                                iconBgColor = CategoryContactContainer,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToHistory
                            )
                        }
                    }
                }

                // Overview Metrics Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Activity Overview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Details →",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onNavigateToStatistics() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // 2x2 Modern KPI Stats Grid
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ModernStatCard(
                                icon = Icons.Rounded.QrCodeScanner,
                                value = stats.totalScans,
                                label = stringResource(id = R.string.total_scans),
                                iconTint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToHistory
                            )
                            ModernStatCard(
                                icon = Icons.Rounded.QrCode,
                                value = stats.totalGenerated,
                                label = stringResource(id = R.string.total_generated),
                                iconTint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToHistory
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ModernStatCard(
                                icon = Icons.Rounded.Star,
                                value = stats.totalFavorites,
                                label = stringResource(id = R.string.total_favorites),
                                iconTint = Color(0xFFFFB300),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToFavorites
                            )
                            ModernStatCard(
                                icon = Icons.Rounded.TrendingUp,
                                value = stats.todayActivity,
                                label = stringResource(id = R.string.scans_today),
                                iconTint = CategoryUrl,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToStatistics
                            )
                        }
                    }
                }

                // Recent Activity Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 20.dp, top = 24.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.recent_scans),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (recentActivity.isNotEmpty()) {
                            Text(
                                text = stringResource(id = R.string.see_all),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onNavigateToHistory() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Recent Activity List with Empty State
                if (recentActivity.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.QrCodeScanner,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = stringResource(id = R.string.no_recent_activity),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = stringResource(id = R.string.no_recent_activity_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = onNavigateToScanner,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.QrCodeScanner,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = stringResource(id = R.string.start_scanning))
                                }
                            }
                        }
                    }
                } else {
                    items(
                        items = recentActivity,
                        key = { activity ->
                            when (activity) {
                                is RecentActivity.Scanned -> "scan_${activity.scan.id}"
                                is RecentActivity.Generated -> "qr_${activity.qr.id}"
                            }
                        }
                    ) { activity ->
                        ModernRecentActivityItem(
                            activity = activity,
                            onClick = {
                                when (activity) {
                                    is RecentActivity.Scanned -> onNavigateToResult(activity.scan)
                                    is RecentActivity.Generated -> onNavigateToGeneratedDetail(activity.qr)
                                }
                            }
                        )
                    }
                }
            }

            // Fullscreen processing indicator for gallery scan
            AnimatedVisibility(
                visible = isProcessingGallery,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Analyzing Image...",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernQuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    iconBgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ModernStatCard(
    icon: ImageVector,
    value: Int,
    label: String,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(18.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ModernRecentActivityItem(
    activity: RecentActivity,
    onClick: () -> Unit
) {
    val isScanned = activity is RecentActivity.Scanned
    val content = if (isScanned) (activity as RecentActivity.Scanned).scan.rawValue else (activity as RecentActivity.Generated).qr.content
    val type = if (isScanned) (activity as RecentActivity.Scanned).scan.scanType else (activity as RecentActivity.Generated).qr.type
    val timestamp = if (isScanned) (activity as RecentActivity.Scanned).scan.timestamp else (activity as RecentActivity.Generated).qr.timestamp

    val (badgeBg, badgeText) = when (type.uppercase()) {
        "URL", "WEBSITE" -> CategoryUrlContainer to CategoryUrl
        "WIFI" -> CategoryWifiContainer to CategoryWifi
        "CONTACT", "VCARD" -> CategoryContactContainer to CategoryContact
        "EMAIL" -> CategoryEmailContainer to CategoryEmail
        "PHONE" -> CategoryPhoneContainer to CategoryPhone
        "SMS" -> CategorySmsContainer to CategorySms
        "GEO", "LOCATION" -> CategoryGeoContainer to CategoryGeo
        else -> CategoryTextContainer to CategoryText
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isScanned) Icons.Rounded.QrCodeScanner else Icons.Rounded.QrCode,
                    contentDescription = null,
                    tint = badgeText,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = badgeText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = DateUtils.formatShort(timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
