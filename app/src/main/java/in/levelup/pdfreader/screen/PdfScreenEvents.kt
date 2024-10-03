package `in`.levelup.pdfreader.screen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

sealed class PdfScreenEvents {
    data class GetTextFromScannedPdf(val pdfBitmaps: List<Bitmap>): PdfScreenEvents()
    data class ExtractTextFromPdfBitmaps(val context: Context, val uri: Uri): PdfScreenEvents()
    data class SpeakText(val text: String): PdfScreenEvents()
    data object PauseSpeaking: PdfScreenEvents()
    data object ResumeSpeaking: PdfScreenEvents()
    data object StopSpeaking: PdfScreenEvents()

}