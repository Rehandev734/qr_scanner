package com.example.scanify.core.qr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlin.math.sqrt

object QRCodeGenerator {

    enum class ModuleStyle { SQUARE, ROUNDED, DOT, DIAMOND }
    enum class EyeStyle { SQUARE, ROUNDED, CIRCLE, MODERN }
    enum class GradientType { LINEAR, RADIAL }

    data class QRConfig(
        val content: String = "",
        val foregroundColor: Int = Color.BLACK,
        val backgroundColor: Int = Color.WHITE,
        val eyeColor: Int = Color.BLACK,
        val moduleStyle: ModuleStyle = ModuleStyle.SQUARE,
        val eyeStyle: EyeStyle = EyeStyle.SQUARE,
        val gradientEnabled: Boolean = false,
        val gradientStartColor: Int = Color.BLACK,
        val gradientEndColor: Int = Color.GRAY,
        val gradientType: GradientType = GradientType.LINEAR,
        val logo: Bitmap? = null,
        val logoPath: String = "",
        val logoSize: Float = 0.2f,
        val frameStyle: String = "",
        val frameText: String = "",
        val size: Int = 1024
    )

    fun generate(config: QRConfig): Bitmap? {
        if (config.content.isBlank()) return null

        return try {
            val hints = HashMap<EncodeHintType, Any>().apply {
                put(EncodeHintType.MARGIN, 0)
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
            }

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(config.content, BarcodeFormat.QR_CODE, config.size, config.size, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val bgColor = if (config.gradientEnabled) config.backgroundColor else config.backgroundColor
            canvas.drawColor(bgColor)

            val moduleSize = width.toFloat() / bitMatrix.width
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            if (config.gradientEnabled) {
                val gradient = when (config.gradientType) {
                    GradientType.LINEAR -> LinearGradient(
                        0f, 0f, width.toFloat(), height.toFloat(),
                        config.gradientStartColor, config.gradientEndColor,
                        Shader.TileMode.CLAMP
                    )
                    GradientType.RADIAL -> RadialGradient(
                        width / 2f, height / 2f, sqrt(width.toFloat() * width.toFloat() + height.toFloat() * height.toFloat()) / 2f,
                        config.gradientStartColor, config.gradientEndColor,
                        Shader.TileMode.CLAMP
                    )
                }
                paint.shader = gradient
            } else {
                paint.color = config.foregroundColor
            }

            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix[x, y]) {
                        val left = x * moduleSize
                        val top = y * moduleSize

                        when (config.moduleStyle) {
                            ModuleStyle.SQUARE -> {
                                canvas.drawRect(left, top, left + moduleSize, top + moduleSize, paint)
                            }
                            ModuleStyle.ROUNDED -> {
                                val rect = RectF(left, top, left + moduleSize, top + moduleSize)
                                canvas.drawRoundRect(rect, moduleSize * 0.3f, moduleSize * 0.3f, paint)
                            }
                            ModuleStyle.DOT -> {
                                canvas.drawCircle(
                                    left + moduleSize / 2, top + moduleSize / 2,
                                    moduleSize / 2 * 0.8f, paint
                                )
                            }
                            ModuleStyle.DIAMOND -> {
                                val path = android.graphics.Path()
                                path.moveTo(left + moduleSize / 2, top)
                                path.lineTo(left + moduleSize, top + moduleSize / 2)
                                path.lineTo(left + moduleSize / 2, top + moduleSize)
                                path.lineTo(left, top + moduleSize / 2)
                                path.close()
                                canvas.drawPath(path, paint)
                            }
                        }
                    }
                }
            }

            if (config.gradientEnabled) {
                paint.shader = null
            }
            paint.color = config.eyeColor

            drawEyes(canvas, bitMatrix, moduleSize, paint, config.eyeStyle)

            config.logo?.let { logo ->
                drawLogo(canvas, bitmap, logo, config.logoSize)
            }

            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun drawEyes(canvas: Canvas, bitMatrix: com.google.zxing.common.BitMatrix, moduleSize: Float, paint: Paint, eyeStyle: EyeStyle) {
        val eyePositions = listOf(
            intArrayOf(0, 0),
            intArrayOf(bitMatrix.width - 7, 0),
            intArrayOf(0, bitMatrix.height - 7)
        )

        for (pos in eyePositions) {
            val startX = pos[0] * moduleSize
            val startY = pos[1] * moduleSize
            val size = 7 * moduleSize

            when (eyeStyle) {
                EyeStyle.SQUARE -> {
                    canvas.drawRect(startX, startY, startX + size, startY + size, paint)
                    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.WHITE
                    }
                    canvas.drawRect(
                        startX + moduleSize, startY + moduleSize,
                        startX + size - moduleSize, startY + size - moduleSize, innerPaint
                    )
                    canvas.drawRect(
                        startX + moduleSize * 2, startY + moduleSize * 2,
                        startX + size - moduleSize * 2, startY + size - moduleSize * 2, paint
                    )
                }
                EyeStyle.ROUNDED -> {
                    val rect = RectF(startX, startY, startX + size, startY + size)
                    canvas.drawRoundRect(rect, moduleSize, moduleSize, paint)
                    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
                    val innerRect = RectF(
                        startX + moduleSize, startY + moduleSize,
                        startX + size - moduleSize, startY + size - moduleSize
                    )
                    canvas.drawRoundRect(innerRect, moduleSize * 0.5f, moduleSize * 0.5f, innerPaint)
                    val centerRect = RectF(
                        startX + moduleSize * 2, startY + moduleSize * 2,
                        startX + size - moduleSize * 2, startY + size - moduleSize * 2
                    )
                    canvas.drawRoundRect(centerRect, moduleSize * 0.3f, moduleSize * 0.3f, paint)
                }
                EyeStyle.CIRCLE -> {
                    val centerX = startX + size / 2
                    val centerY = startY + size / 2
                    canvas.drawCircle(centerX, centerY, size / 2, paint)
                    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
                    canvas.drawCircle(centerX, centerY, size / 2 - moduleSize, innerPaint)
                    canvas.drawCircle(centerX, centerY, size / 2 - moduleSize * 2, paint)
                }
                EyeStyle.MODERN -> {
                    val rect = RectF(startX, startY, startX + size, startY + size)
                    canvas.drawRoundRect(rect, moduleSize * 1.5f, moduleSize * 1.5f, paint)
                    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
                    val innerRect = RectF(
                        startX + moduleSize, startY + moduleSize,
                        startX + size - moduleSize, startY + size - moduleSize
                    )
                    canvas.drawRoundRect(innerRect, moduleSize, moduleSize, innerPaint)
                    val centerRect = RectF(
                        startX + moduleSize * 2, startY + moduleSize * 2,
                        startX + size - moduleSize * 2, startY + size - moduleSize * 2
                    )
                    canvas.drawRoundRect(centerRect, moduleSize * 0.5f, moduleSize * 0.5f, paint)
                }
            }
        }
    }

    private fun drawLogo(canvas: Canvas, qrBitmap: Bitmap, logo: Bitmap, logoSize: Float) {
        val logoWidth = (qrBitmap.width * logoSize).toInt()
        val logoHeight = (qrBitmap.height * logoSize).toInt()
        val logoBitmap = Bitmap.createScaledBitmap(logo, logoWidth, logoHeight, true)

        val left = (qrBitmap.width - logoWidth) / 2f
        val top = (qrBitmap.height - logoHeight) / 2f

        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val bgRect = RectF(left - 8, top - 8, left + logoWidth + 8, top + logoHeight + 8)
        canvas.drawRoundRect(bgRect, 8f, 8f, backgroundPaint)

        canvas.drawBitmap(logoBitmap, left, top, null)
        logoBitmap.recycle()
    }

    fun formatContent(type: String, text: String, ssid: String = "", password: String = "",
                      email: String = "", phone: String = "", subject: String = "", body: String = "",
                      latitude: String = "", longitude: String = "",
                      name: String = "", org: String = "", url: String = ""): String {
        return when (type.uppercase()) {
            "TEXT" -> text
            "WEBSITE" -> if (text.startsWith("http")) text else "https://$text"
            "EMAIL" -> "mailto:$email".let { if (subject.isNotEmpty()) "$it?subject=$subject" else it }
            "PHONE" -> "tel:$phone"
            "SMS" -> "smsto:$phone".let { if (body.isNotEmpty()) "$it?body=$body" else it }
            "WIFI" -> "WIFI:T:WPA;S:$ssid;P:$password;;"
            "CONTACT" -> "BEGIN:VCARD\nVERSION:3.0\nN:$name\nORG:$org\nURL:$url\nEND:VCARD"
            "LOCATION" -> "geo:$latitude,$longitude"
            else -> text
        }
    }
}
