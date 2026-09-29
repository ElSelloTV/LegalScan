package ar.com.elsellotv.legalscan

import android.net.Uri

sealed interface ScanUiState {
    data object Idle : ScanUiState
    data class Ready(val pdfUri: Uri, val pageCount: Int) : ScanUiState
    data class Error(val message: String) : ScanUiState
}
