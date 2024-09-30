package `in`.levelup.pdfreader.screen

import android.graphics.Bitmap

sealed class PdfScreenEvents {
    data class GetTextFromScannedPdf(val pdfBitmaps: List<Bitmap>): PdfScreenEvents()
    data class ExtractTextFromPdfBitmaps(val pdfBitmaps: List<Bitmap>): PdfScreenEvents()
}