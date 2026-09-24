package com.example.scanify.core.qr

import android.graphics.Color

object QRPresets {

    data class ColorPreset(
        val name: String,
        val foreground: Int,
        val background: Int,
        val eye: Int
    )

    val colorPresets = listOf(
        ColorPreset("Classic", Color.BLACK, Color.WHITE, Color.BLACK),
        ColorPreset("Forest", 0xFF1A6B3A.toInt(), Color.WHITE, 0xFF1A6B3A.toInt()),
        ColorPreset("Ocean", 0xFF1565C0.toInt(), Color.WHITE, 0xFF1565C0.toInt()),
        ColorPreset("Sunset", 0xFFE65100.toInt(), Color.WHITE, 0xFFE65100.toInt()),
        ColorPreset("Royal", 0xFF6A1B9A.toInt(), Color.WHITE, 0xFF6A1B9A.toInt()),
        ColorPreset("Rose", 0xFFC62828.toInt(), Color.WHITE, 0xFFC62828.toInt()),
        ColorPreset("Midnight", Color.WHITE, 0xFF1A1A2E.toInt(), Color.WHITE),
        ColorPreset("Charcoal", Color.WHITE, 0xFF424242.toInt(), Color.WHITE),
        ColorPreset("Emerald", 0xFF00695C.toInt(), 0xFFE0F2F1.toInt(), 0xFF00695C.toInt()),
        ColorPreset("Amber", 0xFFF57F17.toInt(), 0xFFFFF8E1.toInt(), 0xFFF57F17.toInt())
    )

    data class Template(
        val name: String,
        val icon: Int,
        val type: String,
        val defaultContent: String = "",
        val moduleStyle: QRCodeGenerator.ModuleStyle = QRCodeGenerator.ModuleStyle.SQUARE,
        val eyeStyle: QRCodeGenerator.EyeStyle = QRCodeGenerator.EyeStyle.SQUARE,
        val colorPreset: ColorPreset = colorPresets[0]
    )

    val templates = listOf(
        Template("Plain Text", 0, "TEXT", "Hello World"),
        Template("Website URL", 0, "WEBSITE", "https://"),
        Template("Wi-Fi Network", 0, "WIFI"),
        Template("Contact Card", 0, "CONTACT"),
        Template("Email", 0, "EMAIL"),
        Template("Phone Number", 0, "PHONE"),
        Template("SMS Message", 0, "SMS"),
        Template("Location", 0, "LOCATION"),
        Template("Business Card", 0, "CONTACT", moduleStyle = QRCodeGenerator.ModuleStyle.ROUNDED, eyeStyle = QRCodeGenerator.EyeStyle.ROUNDED),
        Template("Restaurant Menu", 0, "WEBSITE", moduleStyle = QRCodeGenerator.ModuleStyle.DOT, eyeStyle = QRCodeGenerator.EyeStyle.CIRCLE, colorPreset = colorPresets[1]),
        Template("Instagram", 0, "WEBSITE", "https://instagram.com/", moduleStyle = QRCodeGenerator.ModuleStyle.ROUNDED, eyeStyle = QRCodeGenerator.EyeStyle.MODERN, colorPreset = colorPresets[5]),
        Template("Facebook", 0, "WEBSITE", "https://facebook.com/", colorPreset = colorPresets[2]),
        Template("WhatsApp", 0, "WEBSITE", "https://wa.me/", colorPreset = colorPresets[1]),
        Template("Payment Link", 0, "WEBSITE", moduleStyle = QRCodeGenerator.ModuleStyle.DOT, eyeStyle = QRCodeGenerator.EyeStyle.CIRCLE, colorPreset = colorPresets[2])
    )
}
