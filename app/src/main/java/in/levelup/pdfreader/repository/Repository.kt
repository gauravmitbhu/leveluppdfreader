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
import `in`.levelup.pdfreader.data.roomdatabase.PdfTextDao
import `in`.levelup.pdfreader.model.PdfText
import `in`.levelup.pdfreader.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.io.InputStream
import javax.inject.Inject


class Repository @Inject constructor(private val pdfTextDao: PdfTextDao) {

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
        pdfBitmaps: List<Bitmap>,
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        try {
            for ((index, bitmap) in pdfBitmaps.withIndex()) {
                val image = InputImage.fromBitmap(bitmap, 0)
                val result = recognizer.process(image).await()
                val text = result.text
                Log.d("TAG", "Extracted text from page $index: $text")
                pdfTextDao.insert(PdfText(pageNumber = index + 1, text = text))
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("TAG", "Error recognizing text from image", e)
            emit(Resource.Error(message = "Error processing image: ${e.message}"))
        }
    }


    //room database
    fun getAllDuration(): Flow<List<PdfText>> = pdfTextDao.getAllPdfText()
        .flowOn(Dispatchers.IO)
        .conflate()

//    fun getPdfTextById(pdfId: Int): Flow<List<PdfText>> {
//        return pdfTextDao.getPdfTextById(pdfId)
//    }

}