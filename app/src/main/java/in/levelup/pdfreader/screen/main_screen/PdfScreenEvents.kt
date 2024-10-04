package `in`.levelup.pdfreader.screen.main_screen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import `in`.levelup.pdfreader.model.PdfText

sealed class MainScreenEvents {
    data class GetTextFromScannedPdf(val pdfBitmaps: List<Bitmap>): MainScreenEvents()
    data class ExtractTextFromPdfBitmaps(val context: Context, val uri: Uri): MainScreenEvents()
    data class AddPdf(val pdfText: PdfText): MainScreenEvents()
}