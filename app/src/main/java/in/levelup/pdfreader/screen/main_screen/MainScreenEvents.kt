package `in`.levelup.pdfreader.screen.main_screen

import android.graphics.Bitmap
import `in`.levelup.pdfreader.model.Pdf

sealed class MainScreenEvents {
    data class AddPdf(val pdf: Pdf, val bitmaps: List<Bitmap>): MainScreenEvents()
    data class DeletePdfById(val id: Int): MainScreenEvents()
    data class StorePdfTextWithId(val bitmaps: List<Bitmap>): MainScreenEvents()
    data object GetAllPds: MainScreenEvents()
}