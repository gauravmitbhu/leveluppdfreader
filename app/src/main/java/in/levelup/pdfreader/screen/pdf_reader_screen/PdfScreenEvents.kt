package `in`.levelup.pdfreader.screen.pdf_reader_screen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import `in`.levelup.pdfreader.screen.main_screen.MainScreenEvents

sealed class PdfScreenEvents {
    data class GetPdfTextById(val pdfId: Int): PdfScreenEvents()
    data class SpeakText(val text: String): PdfScreenEvents()
    data object PauseSpeaking: PdfScreenEvents()
    data object ResumeSpeaking: PdfScreenEvents()
    data object StopSpeaking: PdfScreenEvents()
    data object SkipLine: PdfScreenEvents()
    data object PreviousLive: PdfScreenEvents()
}