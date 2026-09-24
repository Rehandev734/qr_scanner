package com.example.scanify.data.mapper

import com.example.scanify.data.local.entity.GeneratedQREntity
import com.example.scanify.domain.model.GeneratedQR

fun GeneratedQREntity.toDomain(): GeneratedQR {
    return GeneratedQR(
        id = id,
        content = content,
        type = type,
        timestamp = timestamp,
        isFavorite = isFavorite,
        foregroundColor = foregroundColor,
        backgroundColor = backgroundColor,
        eyeColor = eyeColor,
        moduleStyle = moduleStyle,
        eyeStyle = eyeStyle,
        gradientEnabled = gradientEnabled,
        gradientStartColor = gradientStartColor,
        gradientEndColor = gradientEndColor,
        gradientType = gradientType,
        logoPath = logoPath,
        frameStyle = frameStyle,
        frameText = frameText,
        templateName = templateName
    )
}

fun GeneratedQR.toEntity(): GeneratedQREntity {
    return GeneratedQREntity(
        id = id,
        content = content,
        type = type,
        timestamp = timestamp,
        isFavorite = isFavorite,
        foregroundColor = foregroundColor,
        backgroundColor = backgroundColor,
        eyeColor = eyeColor,
        moduleStyle = moduleStyle,
        eyeStyle = eyeStyle,
        gradientEnabled = gradientEnabled,
        gradientStartColor = gradientStartColor,
        gradientEndColor = gradientEndColor,
        gradientType = gradientType,
        logoPath = logoPath,
        frameStyle = frameStyle,
        frameText = frameText,
        templateName = templateName
    )
}
