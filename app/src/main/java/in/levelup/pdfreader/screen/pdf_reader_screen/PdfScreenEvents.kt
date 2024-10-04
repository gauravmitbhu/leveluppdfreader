package `in`.levelup.pdfreader.screen.pdf_reader_screen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import `in`.levelup.pdfreader.screen.main_screen.MainScreenEvents

sealed class PdfScreenEvents {
    data class GetTextFromScannedPdf(val pdfBitmaps: List<Bitmap>): PdfScreenEvents()
    data class ExtractTextFromPdfBitmaps(val context: Context, val uri: Uri): PdfScreenEvents()
    data class SpeakText(val text: String): PdfScreenEvents()
    data object PauseSpeaking: PdfScreenEvents()
    data object ResumeSpeaking: PdfScreenEvents()
    data object StopSpeaking: PdfScreenEvents()
    data class GetPdfById(val pdfId: Int): PdfScreenEvents()
}