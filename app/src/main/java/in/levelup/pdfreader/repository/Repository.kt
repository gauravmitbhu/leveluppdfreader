package `in`.levelup.pdfreader.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import `in`.levelup.pdfreader.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class Repository() {

    fun extractTextFromPdfBitmapsAsFlow(pdfBitmaps: List<Bitmap>): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading()) // Start loading state
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val extractedTexts = mutableListOf<String>() // List to store extracted texts

        for (bitmap in pdfBitmaps) {
            val image = InputImage.fromBitmap(bitmap, 0)
            try {
                // Extract text asynchronously
                val result = recognizer.process(image).await() // Wait for the result
                extractedTexts.add(result.text) // Add extracted text to the list
            } catch (e: Exception) {
                e.printStackTrace()
                extractedTexts.add("Error extracting text from bitmap") // Add error message if any
                emit(Resource.Error(e.message))
            }
        }
        emit(Resource.Success(extractedTexts)) // Emit the list of extracted texts once all bitmaps are processed
    }

    fun recognizeTextFromImages(
        pdfBitmaps: List<Bitmap>
    ): Flow<Resource<List<String>>> = flow {

        emit(Resource.Loading()) // Start loading state

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val textResults = mutableListOf<String>() // Store recognized texts

        for (pdfBitmap in pdfBitmaps) {
            val bitmap = pdfBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val image = InputImage.fromBitmap(bitmap, 0)

            try {
                // Use await() to make the process call suspend
                val result = recognizer.process(image).await() // Await the text recognition result
                val recognizedText = result.text // Extract the recognized text

                // If text is empty, return a specific message
                if (recognizedText.isBlank()) {
                    textResults.add("No text recognized")
                } else {
                    textResults.add(recognizedText) // Add recognized text to results
                }

                Log.d("TAG", "Recognized text: $recognizedText")
            } catch (e: Exception) {
                // Log the error and add error message to the results
                Log.e("TAG", "Error recognizing text from image", e)
                textResults.add("Error recognizing text from image")
                emit(Resource.Error(message = "Error processing image: ${e.message}"))
            }
        }

        // Emit the final result when all images are processed
        emit(Resource.Success(textResults))
    }

}