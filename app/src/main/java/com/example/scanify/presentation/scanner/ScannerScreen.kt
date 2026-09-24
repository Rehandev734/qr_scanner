package com.example.scanify.presentation.scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanify.R
import com.example.scanify.data.scanner.BarcodeMapper
import com.example.scanify.domain.model.ScanResult
import com.example.scanify.ui.theme.CategoryUrl
import com.example.scanify.ui.theme.CategoryUrlContainer
import com.example.scanify.ui.theme.ScannerCornerAccent
import com.example.scanify.ui.theme.ScannerGlassBg
import com.example.scanify.ui.theme.ScannerLaser
import com.example.scanify.ui.theme.ScannerLaserGlow
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResult: (ScanResult) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToGenerator: () -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val scanState by viewModel.scanState.collectAsStateWithLifecycle()
    val isFlashOn by viewModel.isFlashOn.collectAsStateWithLifecycle()
    val isContinuousScan by viewModel.isContinuousScan.collectAsStateWithLifecycle()

    var camera by remember { mutableStateOf<Camera?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var currentZoom by remember { mutableFloatStateOf(1f) }
    var showZoomIndicator by remember { mutableStateOf(false) }
    val zoomFadeAlpha = remember { Animatable(1f) }
    var showNoQrDialog by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // Success flash overlay animation
    val flashAlpha = remember { Animatable(0f) }

    // Animated cyber laser sweep line
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_fraction"
    )

    // Breathing corner bracket glow
    val cornerPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corner_pulse"
    )

    // Rotating scan tips
    var currentTipIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            if (viewModel.scanTips.isNotEmpty()) {
                currentTipIndex = (currentTipIndex + 1) % viewModel.scanTips.size
            }
        }
    }

    // Handle scan success & state changes
    LaunchedEffect(scanState) {
        when (val state = scanState) {
            is ScanUiState.Success -> {
                // Play audio & vibration feedback if enabled
                if (viewModel.isSoundEnabled.first()) viewModel.playBeepSound()
                if (viewModel.isVibrationEnabled.first()) viewModel.vibrate()

                // Trigger camera flash effect
                flashAlpha.snapTo(0.85f)
                flashAlpha.animateTo(0f, animationSpec = tween(350))

                if (!isContinuousScan) {
                    onNavigateToResult(state.scan)
                    viewModel.resetState()
                } else {
                    Toast.makeText(context, "Scanned: ${state.scan.rawValue}", Toast.LENGTH_SHORT).show()
                    delay(1500)
                    viewModel.resetState()
                }
            }
            is ScanUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Gallery Picker scanner fallback
    val processGalleryUri: (Uri) -> Unit = { uri ->
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
                    if (barcodes.isNotEmpty()) {
                        viewModel.onBarcodeDetected(barcodes)
                    } else {
                        showNoQrDialog = true
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to decode image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (_: Exception) {
            Toast.makeText(context, "Failed to load image from gallery", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) processGalleryUri(uri)
    }

    val galleryFallbackLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) processGalleryUri(uri)
    }

    val launchGalleryPicker = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            galleryFallbackLauncher.launch("image/*")
        }
    }

    if (showNoQrDialog) {
        AlertDialog(
            onDismissRequest = { showNoQrDialog = false },
            title = {
                Text(text = "No Code Found", style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Text(
                    text = "The selected image does not contain any readable QR code or barcode.",
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (!hasCameraPermission) {
            // Guarded Permission UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(id = R.string.scanner_permission_title),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.scanner_permission_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.grant_permission),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Text(text = "Open App Settings")
                }
            }
        } else {
            // Camera Preview View
            var executorService by remember { mutableStateOf<ExecutorService?>(null) }

            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val exec = Executors.newSingleThreadExecutor()
                    executorService = exec

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val provider = cameraProviderFuture.get()
                        cameraProvider = provider

                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

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
                        val barcodeScanner = BarcodeScanning.getClient(options)

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(exec) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = InputImage.fromMediaImage(
                                            mediaImage,
                                            imageProxy.imageInfo.rotationDegrees
                                        )
                                        barcodeScanner.process(image)
                                            .addOnSuccessListener { barcodes ->
                                                if (barcodes.isNotEmpty()) {
                                                    viewModel.onBarcodeDetected(barcodes)
                                                }
                                            }
                                            .addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        imageProxy.close()
                                    }
                                }
                            }

                        try {
                            provider.unbindAll()
                            camera = provider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                imageAnalysis
                            )
                        } catch (_: Exception) {
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            val cam = camera ?: return@detectTransformGestures
                            val newZoom = (currentZoom * zoom).coerceIn(1f, 5f)
                            currentZoom = newZoom
                            cam.cameraControl.setZoomRatio(newZoom)
                            viewModel.setZoomLevel(newZoom)
                            showZoomIndicator = true

                            scope.launch {
                                zoomFadeAlpha.snapTo(1f)
                                delay(1800)
                                zoomFadeAlpha.animateTo(0f, animationSpec = tween(300))
                                showZoomIndicator = false
                            }
                        }
                    }
            )

            // Strict lifecycle disposal
            DisposableEffect(lifecycleOwner) {
                onDispose {
                    cameraProvider?.unbindAll()
                    executorService?.shutdown()
                }
            }

            // High-tech Reticle Cutout Shader Mask
            val frameSizeDp = 270.dp
            val frameSizePx = with(density) { frameSizeDp.toPx() }
            val cornerRadiusPx = with(density) { 24.dp.toPx() }

            Canvas(modifier = Modifier.fillMaxSize().graphicsLayer(alpha = 0.99f)) {
                val left = (size.width - frameSizePx) / 2f
                val top = (size.height - frameSizePx) / 2f - with(density) { 36.dp.toPx() }
                val right = left + frameSizePx
                val bottom = top + frameSizePx

                // Semi-transparent dark vignette mask
                val path = Path().apply {
                    fillType = PathFillType.EvenOdd
                    addRect(Rect(0f, 0f, size.width, size.height))
                    addRoundRect(
                        RoundRect(
                            rect = Rect(left, top, right, bottom),
                            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                        )
                    )
                }
                drawPath(path, Color.Black.copy(alpha = 0.65f))

                // Subtle inner frame border
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.15f),
                    topLeft = Offset(left, top),
                    size = Size(frameSizePx, frameSizePx),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    style = Stroke(width = with(density) { 1.5.dp.toPx() })
                )

                // High-tech Corner Accents
                val bracketLen = with(density) { 32.dp.toPx() }
                val bracketWidth = with(density) { 3.5.dp.toPx() }
                val cornerColor = ScannerCornerAccent.copy(alpha = cornerPulseAlpha)

                // Top-Left
                drawLine(cornerColor, Offset(left, top + bracketLen), Offset(left, top + cornerRadiusPx), bracketWidth)
                drawArc(
                    color = cornerColor,
                    startAngle = 180f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(left, top),
                    size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                    style = Stroke(bracketWidth)
                )
                drawLine(cornerColor, Offset(left + cornerRadiusPx, top), Offset(left + bracketLen, top), bracketWidth)

                // Top-Right
                drawLine(cornerColor, Offset(right - bracketLen, top), Offset(right - cornerRadiusPx, top), bracketWidth)
                drawArc(
                    color = cornerColor,
                    startAngle = 270f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(right - cornerRadiusPx * 2, top),
                    size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                    style = Stroke(bracketWidth)
                )
                drawLine(cornerColor, Offset(right, top + cornerRadiusPx), Offset(right, top + bracketLen), bracketWidth)

                // Bottom-Left
                drawLine(cornerColor, Offset(left, bottom - bracketLen), Offset(left, bottom - cornerRadiusPx), bracketWidth)
                drawArc(
                    color = cornerColor,
                    startAngle = 90f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(left, bottom - cornerRadiusPx * 2),
                    size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                    style = Stroke(bracketWidth)
                )
                drawLine(cornerColor, Offset(left + cornerRadiusPx, bottom), Offset(left + bracketLen, bottom), bracketWidth)

                // Bottom-Right
                drawLine(cornerColor, Offset(right - bracketLen, bottom), Offset(right - cornerRadiusPx, bottom), bracketWidth)
                drawArc(
                    color = cornerColor,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(right - cornerRadiusPx * 2, bottom - cornerRadiusPx * 2),
                    size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                    style = Stroke(bracketWidth)
                )
                drawLine(cornerColor, Offset(right, bottom - cornerRadiusPx), Offset(right, bottom - bracketLen), bracketWidth)

                // Animated Glowing Laser Sweep Line
                val laserY = top + (bottom - top) * laserFraction
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            ScannerLaserGlow,
                            ScannerLaser
                        ),
                        startY = laserY - with(density) { 20.dp.toPx() },
                        endY = laserY
                    ),
                    topLeft = Offset(left + with(density) { 8.dp.toPx() }, laserY - with(density) { 20.dp.toPx() }),
                    size = Size(frameSizePx - with(density) { 16.dp.toPx() }, with(density) { 20.dp.toPx() })
                )
                drawLine(
                    color = ScannerLaser,
                    start = Offset(left + with(density) { 8.dp.toPx() }, laserY),
                    end = Offset(right - with(density) { 8.dp.toPx() }, laserY),
                    strokeWidth = with(density) { 2.dp.toPx() }
                )
            }

            // Floating Top Glass Island
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = statusBarTop + 12.dp, start = 16.dp, end = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = ScannerGlassBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = stringResource(id = R.string.back),
                                tint = Color.White
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ScannerLaser)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "QR SCANNER",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = Color.White
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val cam = camera ?: return@IconButton
                                    if (!cam.cameraInfo.hasFlashUnit()) {
                                        Toast.makeText(context, "Flash not available", Toast.LENGTH_SHORT).show()
                                        return@IconButton
                                    }
                                    val newFlashState = !isFlashOn
                                    cam.cameraControl.enableTorch(newFlashState)
                                    viewModel.toggleFlash()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isFlashOn) Color(0xFFFFB300) else Color.White.copy(alpha = 0.12f)
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isFlashOn) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                                    contentDescription = stringResource(id = R.string.flashlight),
                                    tint = if (isFlashOn) Color.Black else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Zoom Indicator Badge & Fast Zoom Pills (Placed above bottom bar)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = navBarBottom + 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Floating Zoom Level Display
                AnimatedVisibility(
                    visible = showZoomIndicator,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .alpha(zoomFadeAlpha.value)
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(100.dp))
                    ) {
                        Text(
                            text = String.format("%.1fx Zoom", currentZoom),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                // 1x, 2x, 5x Fast Zoom Selector Row
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(100.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1f, 2f, 5f).forEach { zoomTarget ->
                            val isSelected = kotlin.math.abs(currentZoom - zoomTarget) < 0.3f
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                    )
                                    .clickable {
                                        camera?.cameraControl?.setZoomRatio(zoomTarget)
                                        currentZoom = zoomTarget
                                        viewModel.setZoomLevel(zoomTarget)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${zoomTarget.toInt()}x",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // Floating Scan Tips Pill Banner
            if (viewModel.scanTips.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = navBarBottom + 82.dp, start = 24.dp, end = 24.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = viewModel.scanTips[currentTipIndex],
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Modern Bottom Floating Action Island
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = ScannerGlassBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = navBarBottom + 12.dp, start = 16.dp, end = 16.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                    .shadow(16.dp, RoundedCornerShape(24.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gallery Scan Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { launchGalleryPicker() }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Image,
                                contentDescription = stringResource(id = R.string.scan_from_gallery),
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gallery",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    // History Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigateToHistory() }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = stringResource(id = R.string.history),
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "History",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    // Generate QR Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigateToGenerator() }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.QrCode,
                                contentDescription = stringResource(id = R.string.generate_qr),
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Generate",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Multi-QR Selection Bottom Sheet Card
            val detectedBarcodes by viewModel.detectedBarcodes.collectAsStateWithLifecycle()
            AnimatedVisibility(
                visible = scanState is ScanUiState.MultipleFound && detectedBarcodes.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = navBarBottom + 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(id = R.string.multiple_codes_found, detectedBarcodes.size),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(id = R.string.select_code_to_open),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextButton(onClick = { viewModel.resetState() }) {
                                Text(text = "Dismiss")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(modifier = Modifier.height(180.dp)) {
                            items(detectedBarcodes) { barcode ->
                                val type = BarcodeMapper.formatType(barcode)
                                val value = BarcodeMapper.extractValue(barcode)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { viewModel.processSingleBarcode(barcode) },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = type,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = value,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Success Flash Overlay Animation
            if (flashAlpha.value > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ScannerLaser.copy(alpha = flashAlpha.value))
                )
            }
        }
    }
}
