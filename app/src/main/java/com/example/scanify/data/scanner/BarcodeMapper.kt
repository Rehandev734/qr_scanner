package com.example.scanify.data.scanner

import com.example.scanify.core.constants.ScanType
import com.google.mlkit.vision.barcode.common.Barcode

object BarcodeMapper {

    fun formatType(barcode: Barcode): String {
        return when (barcode.valueType) {
            Barcode.TYPE_URL -> ScanType.URL.displayName
            Barcode.TYPE_PHONE -> ScanType.PHONE.displayName
            Barcode.TYPE_EMAIL -> ScanType.EMAIL.displayName
            Barcode.TYPE_SMS -> ScanType.SMS.displayName
            Barcode.TYPE_WIFI -> ScanType.WIFI.displayName
            Barcode.TYPE_CONTACT_INFO -> ScanType.CONTACT.displayName
            Barcode.TYPE_GEO -> ScanType.GEO.displayName
            Barcode.TYPE_CALENDAR_EVENT -> ScanType.CALENDAR.displayName
            Barcode.TYPE_DRIVER_LICENSE,
            Barcode.TYPE_ISBN,
            Barcode.TYPE_PRODUCT,
            Barcode.TYPE_TEXT -> ScanType.TEXT.displayName
            else -> ScanType.UNKNOWN.displayName
        }
    }

    fun extractValue(barcode: Barcode): String {
        return barcode.rawValue ?: barcode.displayValue ?: ""
    }

    fun getFormatName(format: Int): String {
        return when (format) {
            Barcode.FORMAT_QR_CODE -> "QR Code"
            Barcode.FORMAT_AZTEC -> "Aztec"
            Barcode.FORMAT_EAN_13 -> "EAN-13"
            Barcode.FORMAT_EAN_8 -> "EAN-8"
            Barcode.FORMAT_DATA_MATRIX -> "Data Matrix"
            Barcode.FORMAT_PDF417 -> "PDF417"
            Barcode.FORMAT_UPC_A -> "UPC-A"
            Barcode.FORMAT_UPC_E -> "UPC-E"
            Barcode.FORMAT_CODE_128 -> "Code 128"
            Barcode.FORMAT_CODE_93 -> "Code 93"
            Barcode.FORMAT_CODE_39 -> "Code 39"
            Barcode.FORMAT_CODABAR -> "Codabar"
            Barcode.FORMAT_ITF -> "ITF"
            else -> "Unknown"
        }
    }
}
