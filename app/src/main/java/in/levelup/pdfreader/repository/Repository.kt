package `in`.levelup.pdfreader.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import `in`.levelup.pdfreader.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.InputStream


class Repository {

    fun extractTextFromPdfUriAsFlow(context: Context, pdfUri: Uri): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading())
        val extractedTexts = mutableListOf<String>()
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(pdfUri)
            inputStream?.use {
                val pdfDocument = PdfDocument(PdfReader(it))
                for (pageNumber in 1..pdfDocument.numberOfPages) {
                    val pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(pageNumber))
                    extractedTexts.add(pageText) }
                pdfDocument.close()
                emit(Resource.Success(extractedTexts))
            } ?: throw Exception("Unable to open InputStream from Uri")
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message))
        }
    }

    fun recognizeTextFromImages(
        pdfBitmaps: List<Bitmap>
    ): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading())
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val textResults = mutableListOf<String>()
        for (pdfBitmap in pdfBitmaps) {
            val bitmap = pdfBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val image = InputImage.fromBitmap(bitmap, 0)
            try {
                val result = recognizer.process(image).await()
                val recognizedText = result.text
                if (recognizedText.isBlank()) {
                    textResults.add("No text recognized")
                } else {
                    textResults.add(recognizedText)
                }
                Log.d("TAG", "Recognized text: $recognizedText")
            } catch (e: Exception) {
                Log.e("TAG", "Error recognizing text from image", e)
                textResults.add("Error recognizing text from image")
                emit(Resource.Error(message = "Error processing image: ${e.message}"))
            }
        }
        emit(Resource.Success(textResults))
    }

}