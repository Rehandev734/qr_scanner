package com.example.scanify.presentation.result

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanify.R
import com.example.scanify.core.constants.ScanType
import com.example.scanify.core.utils.DateUtils
import com.example.scanify.core.utils.IntentHelper
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    scanId: Long = 0L,
    scanValue: String = "",
    scanFormat: String = "",
    scanType: String = "",
    scanTimestamp: Long = System.currentTimeMillis(),
    onNavigateBack: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(scanId) {
        if (scanId > 0) {
            viewModel.loadScan(scanId)
        }
    }

    val loadedScan by viewModel.scanResult.collectAsStateWithLifecycle()
    val isAutoCopy by viewModel.isAutoCopy.collectAsStateWithLifecycle()
    val isAutoOpenUrl by viewModel.isAutoOpenUrl.collectAsStateWithLifecycle()

    val currentScan = loadedScan ?: ScanResult(
        id = scanId,
        rawValue = scanValue,
        format = scanFormat,
        scanType = scanType,
        timestamp = scanTimestamp
    )

    var hasAutoCopied by rememberSaveable { mutableStateOf(false) }
    var hasAutoOpened by rememberSaveable { mutableStateOf(false) }
    var isCopiedRecently by remember { mutableStateOf(false) }

    LaunchedEffect(currentScan, isAutoCopy, isAutoOpenUrl) {
        if (currentScan.rawValue.isNotEmpty()) {
            if (isAutoCopy && !hasAutoCopied) {
                hasAutoCopied = true
                try {
                    IntentHelper.copyToClipboard(context, currentScan.rawValue)
                    isCopiedRecently = true
                } catch (_: Exception) {}
            }
            if (isAutoOpenUrl && !hasAutoOpened && currentScan.scanType == ScanType.URL.displayName) {
                hasAutoOpened = true
                try {
                    IntentHelper.openUrl(context, currentScan.rawValue)
                } catch (_: Exception) {}
            }
        }
    }

    LaunchedEffect(isCopiedRecently) {
        if (isCopiedRecently) {
            delay(2000)
            isCopiedRecently = false
        }
    }

    val (badgeBg, badgeTint, categoryIcon) = when (currentScan.scanType.uppercase()) {
        "URL", "WEBSITE" -> Triple(CategoryUrlContainer, CategoryUrl, Icons.Rounded.Language)
        "WIFI" -> Triple(CategoryWifiContainer, CategoryWifi, Icons.Rounded.Wifi)
        "CONTACT", "VCARD" -> Triple(CategoryContactContainer, CategoryContact, Icons.Rounded.Person)
        "EMAIL" -> Triple(CategoryEmailContainer, CategoryEmail, Icons.Rounded.Email)
        "PHONE" -> Triple(CategoryPhoneContainer, CategoryPhone, Icons.Rounded.Phone)
        "SMS" -> Triple(CategorySmsContainer, CategorySms, Icons.Rounded.Sms)
        "GEO", "LOCATION" -> Triple(CategoryGeoContainer, CategoryGeo, Icons.Rounded.Map)
        else -> Triple(CategoryTextContainer, CategoryText, Icons.Rounded.QrCode)
    }

    val isFavorite = currentScan.isFavorite
    val favoriteColor by animateColorAsState(
        targetValue = if (isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "fav_color"
    )

    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Scan Details",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(currentScan.id) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                            contentDescription = "Favorite",
                            tint = favoriteColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    IconButton(onClick = {
                        try {
                            IntentHelper.shareText(context, currentScan.rawValue)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open share dialog", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Main Hero Result Showcase Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = badgeTint.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header Row with Type Badge & Format Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(badgeBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = null,
                                    tint = badgeTint,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = currentScan.scanType.ifEmpty { "Barcode" }.uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = badgeTint
                                )
                                if (currentScan.format.isNotEmpty()) {
                                    Text(
                                        text = currentScan.format,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            Text(
                                text = DateUtils.formatShort(currentScan.timestamp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Decoded Content Container with Selectable Text
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            SelectionContainer {
                                Text(
                                    text = currentScan.rawValue,
                                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${currentScan.rawValue.length} characters",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isCopiedRecently) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                                    modifier = Modifier.clickable {
                                        try {
                                            IntentHelper.copyToClipboard(context, currentScan.rawValue)
                                            isCopiedRecently = true
                                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                        } catch (_: Exception) {}
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isCopiedRecently) Icons.Rounded.Check else Icons.Rounded.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = if (isCopiedRecently) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isCopiedRecently) "Copied!" else "Copy",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isCopiedRecently) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contextual Primary Action Button
            val primaryAction = getPrimaryAction(currentScan)
            if (primaryAction != null) {
                Button(
                    onClick = {
                        try {
                            primaryAction.action(context, currentScan.rawValue)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot open: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = badgeTint,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = primaryAction.icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = primaryAction.title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Actions 2x2 Grid
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                ModernResultActionButton(
                    icon = Icons.Rounded.ContentCopy,
                    label = "Copy Text",
                    iconTint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        try {
                            IntentHelper.copyToClipboard(context, currentScan.rawValue)
                            isCopiedRecently = true
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        } catch (_: Exception) {}
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                ModernResultActionButton(
                    icon = Icons.Rounded.Share,
                    label = "Share",
                    iconTint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        try {
                            IntentHelper.shareText(context, currentScan.rawValue)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Failed to share", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                ModernResultActionButton(
                    icon = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    label = if (isFavorite) "Saved in Favs" else "Add Favorite",
                    iconTint = if (isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.toggleFavorite(currentScan.id) }
                )
                Spacer(modifier = Modifier.width(12.dp))
                ModernResultActionButton(
                    icon = Icons.Rounded.Search,
                    label = "Google Search",
                    iconTint = CategoryUrl,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        try {
                            IntentHelper.openUrl(context, "https://www.google.com/search?q=${java.net.URLEncoder.encode(currentScan.rawValue, "UTF-8")}")
                        } catch (_: Exception) {}
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Metadata Detail Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Technical Metadata",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    MetadataRow(label = "Format", value = currentScan.format.ifEmpty { "QR_CODE" })
                    MetadataRow(label = "Category", value = currentScan.scanType)
                    MetadataRow(label = "Scanned At", value = DateUtils.formatFull(currentScan.timestamp))
                    if (currentScan.id > 0) {
                        MetadataRow(label = "Record ID", value = "#${currentScan.id}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(navBarBottom + 24.dp))
        }
    }
}

private data class SmartAction(
    val title: String,
    val icon: ImageVector,
    val action: (android.content.Context, String) -> Unit
)

private fun getPrimaryAction(scan: ScanResult): SmartAction? {
    return when (scan.scanType.uppercase()) {
        "URL", "WEBSITE" -> SmartAction("Open in Browser", Icons.Rounded.OpenInBrowser) { ctx, url ->
            IntentHelper.openUrl(ctx, url)
        }
        "PHONE" -> SmartAction("Call Phone Number", Icons.Rounded.Phone) { ctx, number ->
            IntentHelper.dialNumber(ctx, number)
        }
        "EMAIL" -> SmartAction("Send Email", Icons.Rounded.Email) { ctx, email ->
            IntentHelper.sendEmail(ctx, email)
        }
        "SMS" -> SmartAction("Send SMS Message", Icons.Rounded.Sms) { ctx, sms ->
            IntentHelper.sendSms(ctx, sms)
        }
        "WIFI" -> SmartAction("Connect to Wi-Fi", Icons.Rounded.Wifi) { ctx, creds ->
            IntentHelper.openUrl(ctx, creds)
        }
        "GEO", "LOCATION" -> SmartAction("View on Google Maps", Icons.Rounded.Map) { ctx, geo ->
            IntentHelper.openMaps(ctx, geo)
        }
        "CONTACT", "VCARD" -> SmartAction("Add to Contacts", Icons.Rounded.Person) { ctx, vcard ->
            IntentHelper.shareText(ctx, vcard)
        }
        else -> {
            if (scan.rawValue.startsWith("http://") || scan.rawValue.startsWith("https://")) {
                SmartAction("Open in Browser", Icons.Rounded.OpenInBrowser) { ctx, url ->
                    IntentHelper.openUrl(ctx, url)
                }
            } else {
                SmartAction("Search on Google", Icons.Rounded.Search) { ctx, text ->
                    IntentHelper.openUrl(ctx, "https://www.google.com/search?q=${java.net.URLEncoder.encode(text, "UTF-8")}")
                }
            }
        }
    }
}

@Composable
private fun ModernResultActionButton(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
