package com.example.scanify.presentation.history

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanify.core.utils.DateUtils
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

@Composable
fun ScanHistoryItem(
    scan: ScanResult,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isSelected: Boolean = false,
    isMultiSelectMode: Boolean = false,
    onToggleSelect: () -> Unit = {}
) {
    val (badgeBg, badgeTint, categoryIcon) = getScanCategoryTheme(scan.scanType)

    val favoriteColor by animateColorAsState(
        targetValue = if (scan.isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "fav_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                if (isMultiSelectMode) onToggleSelect() else onClick()
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        } else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isMultiSelectMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = scan.scanType,
                    tint = badgeTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = scan.scanType.uppercase(),
                            color = badgeTint,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = DateUtils.formatShort(scan.timestamp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = scan.rawValue,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!isMultiSelectMode) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = if (scan.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                        contentDescription = "Favorite",
                        tint = favoriteColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.75f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

fun getScanCategoryTheme(scanType: String): Triple<Color, Color, ImageVector> {
    return when (scanType.uppercase().replace("-", "").replace(" ", "")) {
        "URL", "WEBSITE" -> Triple(CategoryUrlContainer, CategoryUrl, Icons.Rounded.Language)
        "WIFI" -> Triple(CategoryWifiContainer, CategoryWifi, Icons.Rounded.Wifi)
        "CONTACT", "VCARD" -> Triple(CategoryContactContainer, CategoryContact, Icons.Rounded.Person)
        "EMAIL" -> Triple(CategoryEmailContainer, CategoryEmail, Icons.Rounded.Email)
        "PHONE" -> Triple(CategoryPhoneContainer, CategoryPhone, Icons.Rounded.Phone)
        "SMS" -> Triple(CategorySmsContainer, CategorySms, Icons.Rounded.Sms)
        "GEO", "LOCATION" -> Triple(CategoryGeoContainer, CategoryGeo, Icons.Rounded.Map)
        else -> Triple(CategoryTextContainer, CategoryText, Icons.Rounded.QrCode)
    }
}
