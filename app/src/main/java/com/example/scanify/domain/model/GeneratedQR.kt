package com.example.scanify.domain.model

data class GeneratedQR(
    val id: Long = 0,
    val content: String,
    val type: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val foregroundColor: Int = 0xFF000000.toInt(),
    val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    val eyeColor: Int = 0xFF000000.toInt(),
    val moduleStyle: String = "SQUARE",
    val eyeStyle: String = "SQUARE",
    val gradientEnabled: Boolean = false,
    val gradientStartColor: Int = 0xFF000000.toInt(),
    val gradientEndColor: Int = 0xFF666666.toInt(),
    val gradientType: String = "LINEAR",
    val logoPath: String = "",
    val frameStyle: String = "",
    val frameText: String = "",
    val templateName: String = ""
)
