package `in`.levelup.pdfreader.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import `in`.levelup.pdfreader.data.roomdatabase.PdfTextDao
import `in`.levelup.pdfreader.model.Pdf
import `in`.levelup.pdfreader.model.PdfText
import `in`.levelup.pdfreader.model.PdfsWithText
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

    /*fun extractTextFromPdfUriAsFlow(
        id: Int,
        context: Context,
        pdfUri: Uri
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(pdfUri)
            inputStream?.use {
                val pdfDocument = PdfDocument(PdfReader(it))
                for (pageNumber in 1..pdfDocument.numberOfPages) {
                    val pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(pageNumber))
                    pdfTextDao.insertPdfText(
                        PdfText(
                            pageNumber = pageNumber + 1,
                            text = pageText,
                            pdfId = id
                        )
                    )
                }
                pdfDocument.close()
                emit(Resource.Success(Unit))
            } ?: throw Exception("Unable to open InputStream from Uri")
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message))
        }
    }*/

    fun recognizeTextFromImages(id: Int, pdfBitmaps: List<Bitmap>, language: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        val recognizer = when (language.lowercase()) {
            "chinese" -> TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
            "devanagari" -> TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
            "japanese" -> TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
            "korean" -> TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
            else -> {
                emit(Resource.Error("Unsupported language: $language"))
                return@flow
            }
        }

        try {
            pdfBitmaps.forEachIndexed { index, bitmap ->
                val image = InputImage.fromBitmap(bitmap, 0)
                val result = recognizer.process(image).await()
                val text = result.text
                pdfTextDao.insertPdfText(
                    PdfText(
                        pageNumber = index + 1,
                        text = text,
                        pdfId = id
                    )
                )
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("TAG", "Error recognizing text from image", e)
            emit(Resource.Error(message = "Error processing image: ${e.message}"))
        }
    }

    // room database
    fun insertPdf(pdf: Pdf): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            pdfTextDao.insertPdf(pdf = pdf)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun deletePdfById(pdfId: Int): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            pdfTextDao.deletePdf(pdfId = pdfId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getAllPdf(): Flow<List<Pdf>> = pdfTextDao.getAllPdf()
        .flowOn(Dispatchers.IO)
        .conflate()

    fun getLatestPdfEntry(): Flow<Resource<Pdf>> = flow {
        emit(Resource.Loading())
        try {
            val result = pdfTextDao.getLatestPdf()
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getPdfTextById(id: Int): Flow<Resource<List<PdfsWithText>>> = flow {
        emit(Resource.Loading())
        try {
            val result = pdfTextDao.getPdfWithText(pdfId = id)
            emit(Resource.Success(result))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }
}
