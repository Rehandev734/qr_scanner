package com.example.scanify.core.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.scanify.domain.model.GeneratedQR
import com.example.scanify.domain.model.ScanResult
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

object ExportUtils {

    fun exportBitmapAsPng(context: Context, bitmap: Bitmap, filename: String = "qr_export_${System.currentTimeMillis()}.png"): Uri? {
        return try {
            val file = File(context.cacheDir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            getFileUri(context, file)
        } catch (e: Exception) {
            null
        }
    }

    fun exportBitmapAsJpeg(context: Context, bitmap: Bitmap, quality: Int = 90, filename: String = "qr_export_${System.currentTimeMillis()}.jpg"): Uri? {
        return try {
            val file = File(context.cacheDir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
            getFileUri(context, file)
        } catch (e: Exception) {
            null
        }
    }

    fun shareImage(context: Context, uri: Uri, title: String = "Share Image") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun exportScansToCsv(context: Context, scans: List<ScanResult>): Uri? {
        return try {
            val file = File(context.cacheDir, "scan_history_${System.currentTimeMillis()}.csv")
            FileWriter(file).use { writer ->
                writer.append("ID,Type,Content,Format,Timestamp,Favorite\n")
                scans.forEach { scan ->
                    writer.append("${scan.id},\"${escapeCsv(scan.scanType)}\",\"${escapeCsv(scan.rawValue)}\",\"${escapeCsv(scan.format)}\",${scan.timestamp},${scan.isFavorite}\n")
                }
            }
            getFileUri(context, file)
        } catch (e: Exception) {
            null
        }
    }

    fun exportGeneratedToCsv(context: Context, items: List<GeneratedQR>): Uri? {
        return try {
            val file = File(context.cacheDir, "generated_qr_${System.currentTimeMillis()}.csv")
            FileWriter(file).use { writer ->
                writer.append("ID,Type,Content,Timestamp,Favorite\n")
                items.forEach { qr ->
                    writer.append("${qr.id},\"${escapeCsv(qr.type)}\",\"${escapeCsv(qr.content)}\",${qr.timestamp},${qr.isFavorite}\n")
                }
            }
            getFileUri(context, file)
        } catch (e: Exception) {
            null
        }
    }

    fun exportMixedToCsv(context: Context, scans: List<ScanResult>, generated: List<GeneratedQR>): Uri? {
        return try {
            val file = File(context.cacheDir, "all_history_${System.currentTimeMillis()}.csv")
            FileWriter(file).use { writer ->
                writer.append("ID,Source,Type,Content,Format/Timestamp,Timestamp,Favorite\n")
                scans.forEach { scan ->
                    writer.append("${scan.id},Scanned,\"${escapeCsv(scan.scanType)}\",\"${escapeCsv(scan.rawValue)}\",\"${escapeCsv(scan.format)}\",${scan.timestamp},${scan.isFavorite}\n")
                }
                generated.forEach { qr ->
                    writer.append("${qr.id},Generated,\"${escapeCsv(qr.type)}\",\"${escapeCsv(qr.content)}\",\"\",${qr.timestamp},${qr.isFavorite}\n")
                }
            }
            getFileUri(context, file)
        } catch (e: Exception) {
            null
        }
    }

    fun shareCsv(context: Context, uri: Uri, title: String = "Share CSV") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"")
    }

    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
