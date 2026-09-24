package com.example.scanify.presentation.home

import com.example.scanify.domain.model.GeneratedQR
import com.example.scanify.domain.model.ScanResult

sealed class RecentActivity(val timestamp: Long) {
    data class Scanned(val scan: ScanResult) : RecentActivity(scan.timestamp)
    data class Generated(val qr: GeneratedQR) : RecentActivity(qr.timestamp)
}
