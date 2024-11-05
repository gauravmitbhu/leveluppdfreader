package `in`.levelup.pdfreader.screen.main_screen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import `in`.levelup.pdfreader.model.Pdf

sealed class MainScreenEvents {
    data class AddPdf(val pdf: Pdf, val bitmaps: List<Bitmap>): MainScreenEvents()
    data class ExtractTextFromPdf(val context: Context,val pdfUri: Uri): MainScreenEvents()
    data class GetTextFromPdfBitMaps(val bitmaps: List<Bitmap>): MainScreenEvents()
    data class StorePdfTextWithId(val bitmaps: List<Bitmap>): MainScreenEvents()
    data object GetAllPds: MainScreenEvents()
}