package com.example.scanify.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// Material 3 Expressive Light Color Palette
// ==========================================
val PrimaryLight = Color(0xFF0A7E43)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFA2F7BE)
val OnPrimaryContainerLight = Color(0xFF00210E)

val SecondaryLight = Color(0xFF006A6F)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFF9CF1F8)
val OnSecondaryContainerLight = Color(0xFF002022)

val TertiaryLight = Color(0xFF8B4F00)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFDCC1)
val OnTertiaryContainerLight = Color(0xFF2E1500)

val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)

val BackgroundLight = Color(0xFFF7FBF4)
val OnBackgroundLight = Color(0xFF181D19)
val SurfaceLight = Color(0xFFF7FBF4)
val OnSurfaceLight = Color(0xFF181D19)
val SurfaceVariantLight = Color(0xFFDCE5DC)
val OnSurfaceVariantLight = Color(0xFF404942)
val OutlineLight = Color(0xFF707A72)
val OutlineVariantLight = Color(0xFFC0C9C0)

val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFF1F5EE)
val SurfaceContainerLight = Color(0xFFEBEFE8)
val SurfaceContainerHighLight = Color(0xFFE6E9E3)
val SurfaceContainerHighestLight = Color(0xFFE0E4DD)

// =========================================
// Material 3 Expressive Dark Color Palette
// =========================================
val PrimaryDark = Color(0xFF4AE088)
val OnPrimaryDark = Color(0xFF003919)
val PrimaryContainerDark = Color(0xFF005229)
val OnPrimaryContainerDark = Color(0xFFA2F7BE)

val SecondaryDark = Color(0xFF80D5DC)
val OnSecondaryDark = Color(0xFF003639)
val SecondaryContainerDark = Color(0xFF004F54)
val OnSecondaryContainerDark = Color(0xFF9CF1F8)

val TertiaryDark = Color(0xFFFFB77C)
val OnTertiaryDark = Color(0xFF4B2700)
val TertiaryContainerDark = Color(0xFF6B3B00)
val OnTertiaryContainerDark = Color(0xFFFFDCC1)

val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

val BackgroundDark = Color(0xFF0F1411)
val OnBackgroundDark = Color(0xFFE0E4DE)
val SurfaceDark = Color(0xFF0F1411)
val OnSurfaceDark = Color(0xFFE0E4DE)
val SurfaceVariantDark = Color(0xFF404942)
val OnSurfaceVariantDark = Color(0xFFC0C9C0)
val OutlineDark = Color(0xFF8A938B)
val OutlineVariantDark = Color(0xFF404942)

val SurfaceContainerLowestDark = Color(0xFF0A0F0C)
val SurfaceContainerLowDark = Color(0xFF171D19)
val SurfaceContainerDark = Color(0xFF1B211D)
val SurfaceContainerHighDark = Color(0xFF262B27)
val SurfaceContainerHighestDark = Color(0xFF313632)

// =========================================
// Semantic Barcode & Category Colors
// =========================================
val CategoryUrl = Color(0xFF1976D2)
val CategoryWifi = Color(0xFF7B1FA2)
val CategoryContact = Color(0xFFE65100)
val CategoryEmail = Color(0xFFC62828)
val CategoryPhone = Color(0xFF00796B)
val CategorySms = Color(0xFF0288D1)
val CategoryGeo = Color(0xFF2E7D32)
val CategoryText = Color(0xFF546E7A)

val CategoryLocation = CategoryGeo

val CategoryUrlContainer = Color(0xFFE3F2FD)
val CategoryWifiContainer = Color(0xFFF3E5F5)
val CategoryContactContainer = Color(0xFFFFF3E0)
val CategoryEmailContainer = Color(0xFFFFEBEE)
val CategoryPhoneContainer = Color(0xFFE0F2F1)
val CategorySmsContainer = Color(0xFFE1F5FE)
val CategoryGeoContainer = Color(0xFFE8F5E9)
val CategoryTextContainer = Color(0xFFECEFF1)

// =========================================
// Scanner HUD & Cyber Visual Effects
// =========================================
val ScannerLaser = Color(0xFF00E676)
val ScannerLaserGlow = Color(0x6600E676)
val ScannerCornerAccent = Color(0xFF00E676)
val ScannerReticleDark = Color(0x99000000)
val ScannerGlassBg = Color(0xCC0E1310)
val ScannerElectricCyan = Color(0xFF00E5FF)
val ScannerNeonPurple = Color(0xFF7C4DFF)

// Gradients
val EmeraldGlowGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF0A7E43), Color(0xFF004D27))
)

val HeroScanGradientLight = Brush.horizontalGradient(
    colors = listOf(Color(0xFF0A7E43), Color(0xFF006A6F))
)

val HeroScanGradientDark = Brush.horizontalGradient(
    colors = listOf(Color(0xFF005229), Color(0xFF004F54))
)

// Backward Compatibility Aliases
val ScannerOverlay = Color(0x99000000)
val ScannerFrame = Color(0xFF00E676)
val ScannerFrameDark = Color(0xFF4AE088)
val UrlColor = CategoryUrl
val PhoneColor = CategoryPhone
val EmailColor = CategoryEmail
val WifiColor = CategoryWifi
val ContactColor = CategoryContact
val FavoriteStar = Color(0xFFFFB300)
val FavoriteStarDark = Color(0xFFFFC107)

val StatScansBgLight = Color(0xFFE8F5E9)
val StatGeneratedBgLight = Color(0xFFE0F2F1)
val StatFavoritesBgLight = Color(0xFFFFF8E1)
val StatTodayBgLight = Color(0xFFE3F2FD)

val StatScansBgDark = Color(0xFF142419)
val StatGeneratedBgDark = Color(0xFF122426)
val StatFavoritesBgDark = Color(0xFF282212)
val StatTodayBgDark = Color(0xFF122128)

val QuickActionScanBg = Color(0xFFE8F5E9)
val QuickActionGenerateBg = Color(0xFFE0F2F1)
val QuickActionGalleryBg = Color(0xFFE3F2FD)
val QuickActionHistoryBg = Color(0xFFFFF3E0)
