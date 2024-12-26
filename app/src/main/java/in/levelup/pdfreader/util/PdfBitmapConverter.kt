package `in`.levelup.pdfreader.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class PdfBitmapConverter(
    private val context: Context
) {
    var renderer: PdfRenderer? = null

    suspend fun pdfToBitmaps(contentUri: Uri): List<Bitmap> {
        return withContext(Dispatchers.IO) {
            renderer?.close()

            context.contentResolver.openFileDescriptor(contentUri, "r")?.use { descriptor ->
                PdfRenderer(descriptor).use { pdfRenderer ->
                    renderer = pdfRenderer

                    (0 until pdfRenderer.pageCount).map { index ->
                        async {
                            synchronized(pdfRenderer) {
                                pdfRenderer.openPage(index).use { page ->
                                    val bitmap = Bitmap.createBitmap(
                                        page.width,
                                        page.height,
                                        Bitmap.Config.ARGB_8888
                                    )

                                    page.render(
                                        bitmap,
                                        null,
                                        null,
                                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                    )

                                    bitmap
                                }
                            }
                        }
                    }.awaitAll()
                }
            } ?: emptyList()
        }
    }

}