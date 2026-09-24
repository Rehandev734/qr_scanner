package com.example.scanify.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object IntentHelper {

    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show()
        }
    }

    fun dialNumber(context: Context, phoneNumber: String) {
        try {
            val number = phoneNumber.removePrefix("tel:")
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot dial number", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(context: Context, email: String, subject: String = "", body: String = "") {
        try {
            val address = email.removePrefix("mailto:")
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSms(context: Context, phoneNumber: String, message: String = "") {
        try {
            val number = phoneNumber.removePrefix("smsto:").removePrefix("sms:")
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$number")
                putExtra("sms_body", message)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No SMS app found", Toast.LENGTH_SHORT).show()
        }
    }

    fun openMaps(context: Context, geoUri: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open maps", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Scan Result", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareText(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }
}
