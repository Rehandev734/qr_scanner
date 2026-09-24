package com.example.scanify.core.constants

enum class ScanType(val displayName: String) {
    URL("URL"),
    PHONE("Phone"),
    EMAIL("Email"),
    SMS("SMS"),
    WIFI("Wi-Fi"),
    CONTACT("Contact"),
    GEO("Location"),
    CALENDAR("Calendar"),
    TEXT("Text"),
    UNKNOWN("Unknown");

    companion object {
        fun fromRawValue(value: String): ScanType {
            val lower = value.lowercase()
            return when {
                lower.startsWith("http://") || lower.startsWith("https://") -> URL
                lower.startsWith("tel:") -> PHONE
                lower.startsWith("mailto:") -> EMAIL
                lower.startsWith("smsto:") || lower.startsWith("sms:") -> SMS
                lower.startsWith("wifi:") -> WIFI
                lower.startsWith("begin:vcard") -> CONTACT
                lower.startsWith("geo:") -> GEO
                lower.startsWith("begin:vevent") -> CALENDAR
                else -> TEXT
            }
        }
    }
}
