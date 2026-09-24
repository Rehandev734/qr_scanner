package com.example.scanify.presentation.generated

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanify.R
import com.example.scanify.core.utils.DateUtils
import com.example.scanify.core.utils.IntentHelper
import com.example.scanify.domain.model.GeneratedQR
import com.example.scanify.ui.theme.CategoryContact
import com.example.scanify.ui.theme.CategoryEmail
import com.example.scanify.ui.theme.CategoryLocation
import com.example.scanify.ui.theme.CategoryPhone
import com.example.scanify.ui.theme.CategorySms
import com.example.scanify.ui.theme.CategoryText
import com.example.scanify.ui.theme.CategoryUrl
import com.example.scanify.ui.theme.CategoryWifi
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun GeneratedDetailScreen(
    qrId: Long = 0L,
    qrContent: String = "",
    qrType: String = "",
    qrTimestamp: Long = System.currentTimeMillis(),
    qrIsFavorite: Boolean = false,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (id: Long, content: String, type: String, isFavorite: Boolean) -> Unit,
    viewModel: GeneratedDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(qrId) {
        if (qrId > 0) {
            viewModel.loadGenerated(qrId)
        } else {
            viewModel.loadFromArguments(qrContent, qrType, qrTimestamp, qrIsFavorite)
        }
    }

    val loadedQR by viewModel.generatedQR.collectAsStateWithLifecycle()
    val qrBitmap by viewModel.qrBitmap.collectAsStateWithLifecycle()

    val currentQr = loadedQR ?: GeneratedQR(
        id = qrId,
        content = qrContent,
        type = qrType,
        timestamp = qrTimestamp,
        isFavorite = qrIsFavorite
    )

    val categoryColor = getGeneratedDetailTypeColor(currentQr.type)
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = stringResource(id = R.string.generator_delete_confirm_title),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.generator_delete_confirm_desc),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteGenerated()
                        onNavigateBack()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.delete),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = stringResource(id = R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = stringResource(id = R.string.qr_detail),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        if (currentQr.id > 0) {
                            viewModel.toggleFavorite(currentQr.id)
                            val newFav = !currentQr.isFavorite
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (newFav) context.getString(R.string.generator_favorite_added)
                                    else context.getString(R.string.generator_favorite_removed)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (currentQr.isFavorite) R.drawable.ic_star else R.drawable.ic_star_empty
                        ),
                        contentDescription = null,
                        tint = if (currentQr.isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large QR Image Showcase Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.size(250.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        qrBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = stringResource(id = R.string.generate_qr),
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: run {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_qr_code),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Type Badge & Formats
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = categoryColor.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        categoryColor.copy(alpha = 0.25f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(categoryColor)
                        )
                        Text(
                            text = currentQr.type.uppercase(),
                            color = categoryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Content Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.content).uppercase(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SelectionContainer {
                            Text(
                                text = currentQr.content,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                // Date Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_history),
                                contentDescription = stringResource(id = R.string.generator_detail_created),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = stringResource(id = R.string.generator_detail_created).uppercase(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = DateUtils.formatFull(currentQr.timestamp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.generator_detail_actions).uppercase(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Primary Actions Row: Edit and Favorite
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            onNavigateToEdit(
                                currentQr.id,
                                currentQr.content,
                                currentQr.type,
                                currentQr.isFavorite
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.edit_qr),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            if (currentQr.id > 0) {
                                viewModel.toggleFavorite(currentQr.id)
                                val newFav = !currentQr.isFavorite
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (newFav) context.getString(R.string.generator_favorite_added)
                                        else context.getString(R.string.generator_favorite_removed)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (currentQr.isFavorite) R.drawable.ic_star else R.drawable.ic_star_empty
                            ),
                            contentDescription = null,
                            tint = if (currentQr.isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentQr.isFavorite) stringResource(id = R.string.remove_from_favorites)
                            else stringResource(id = R.string.add_to_favorites),
                            maxLines = 1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Secondary Actions List
                // 1. Copy Content
                DetailActionCard(
                    iconRes = R.drawable.ic_copy,
                    title = stringResource(id = R.string.copy),
                    subtitle = stringResource(id = R.string.copy_content),
                    iconTint = MaterialTheme.colorScheme.primary,
                    iconBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    onClick = {
                        try {
                            IntentHelper.copyToClipboard(context, currentQr.content)
                            scope.launch { snackbarHostState.showSnackbar("Copied to clipboard") }
                        } catch (_: Exception) {}
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Share QR Image
                DetailActionCard(
                    iconRes = R.drawable.ic_share,
                    title = stringResource(id = R.string.share),
                    subtitle = stringResource(id = R.string.share_qr_image),
                    iconTint = CategoryUrl,
                    iconBg = CategoryUrl.copy(alpha = 0.12f),
                    onClick = {
                        val bitmap = qrBitmap
                        if (bitmap != null) {
                            try {
                                val file = File(context.cacheDir, "qr_share_${System.currentTimeMillis()}.png")
                                FileOutputStream(file).use { out ->
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                                }
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "image/png"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, context.getString(R.string.generator_share_title)))
                            } catch (_: Exception) {
                                IntentHelper.shareText(context, currentQr.content)
                            }
                        } else {
                            IntentHelper.shareText(context, currentQr.content)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Download QR Image
                DetailActionCard(
                    iconRes = R.drawable.ic_download,
                    title = stringResource(id = R.string.download),
                    subtitle = stringResource(id = R.string.download_qr_image),
                    iconTint = CategoryWifi,
                    iconBg = CategoryWifi.copy(alpha = 0.12f),
                    onClick = {
                        qrBitmap?.let {
                            viewModel.saveBitmap(it)
                            scope.launch { snackbarHostState.showSnackbar("QR Code saved to gallery") }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 4. Delete
                DetailActionCard(
                    iconRes = R.drawable.ic_delete,
                    title = stringResource(id = R.string.delete),
                    subtitle = stringResource(id = R.string.delete_permanently),
                    iconTint = MaterialTheme.colorScheme.error,
                    iconBg = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                    onClick = {
                        showDeleteDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DetailActionCard(
    iconRes: Int,
    title: String,
    subtitle: String,
    iconTint: Color,
    iconBg: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
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
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(18.dp)
                    .rotate(180f)
            )
        }
    }
}

private fun getGeneratedDetailTypeColor(type: String): Color {
    return when (type.uppercase()) {
        "WEBSITE" -> CategoryUrl
        "PHONE" -> CategoryPhone
        "EMAIL" -> CategoryEmail
        "WIFI" -> CategoryWifi
        "SMS" -> CategorySms
        "TEXT" -> CategoryText
        "CONTACT" -> CategoryContact
        "LOCATION" -> CategoryLocation
        else -> CategoryText
    }
}
