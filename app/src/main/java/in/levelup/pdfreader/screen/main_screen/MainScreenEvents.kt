package `in`.levelup.pdfreader.screen.main_screen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import `in`.levelup.pdfreader.model.Pdf

sealed class MainScreenEvents {
    data class InsertScannedPdf(
        val pdf: Pdf,
        val bitmaps: List<Bitmap>,
        val language: String
    ) : MainScreenEvents()
    data class StoreScannedPdfTextWithId(
        val bitmaps: List<Bitmap>,
        val language: String
    ) : MainScreenEvents()
   /* data class InsertExtractedPdf(
        val pdf: Pdf,
        val context: Context,
        val pdfUri: Uri
    ) : MainScreenEvents()
    data class StoreExtractedPdfTextWithId(
        val context: Context,
        val pdfUri: Uri
    ) : MainScreenEvents()*/
    data class DeletePdfById(val id: Int) : MainScreenEvents()
    data object GetAllPds : MainScreenEvents()
}