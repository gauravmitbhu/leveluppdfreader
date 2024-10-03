package `in`.levelup.pdfreader.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TTSManager @Inject constructor(private val context: Context) {

    private var textToSpeech: TextToSpeech? = null
    var isInitialized = false
    private var ttsListener: TTSListener? = null

    private var remainingText: String = ""
    private var spokenText = "" // To keep track of spoken text
    private var isPaused = false

    fun setTTSListener(listener: TTSListener) {
        this.ttsListener = listener
    }

    private val speechListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            Log.d("TTSManager", "onStart: Speaking started")
        }

        override fun onDone(utteranceId: String?) {
            Log.d("TTSManager", "onDone: Speaking finished")
            // Mark that a chunk has been spoken
            spokenText += extractNextChunk(spokenText.length, remainingText)
            Log.d("TTSManager", "Spoken text updated: $spokenText")

            if (spokenText.length < remainingText.length && !isPaused) {
                // Continue speaking the next chunk
                val nextChunk = extractNextChunk(spokenText.length, remainingText)
                Log.d("TTSManager", "Continuing with next chunk: $nextChunk")
                textToSpeech?.speak(nextChunk, TextToSpeech.QUEUE_FLUSH, null, "ttsSpeakNext")
            } else {
                // If all text has been spoken or TTS is paused, notify listener
                if (spokenText.length >= remainingText.length) {
                    ttsListener?.onTTSFinished()
                    reset()
                }
            }
        }

        override fun onError(utteranceId: String?) {
            Log.e("TTSManager", "onError: Error while speaking")
        }
    }

    fun init() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.getDefault()
                textToSpeech?.setSpeechRate(1.0f)
                isInitialized = true
                Log.d("TTSManager", "TTS initialized successfully")
                textToSpeech?.setOnUtteranceProgressListener(speechListener)
            } else {
                Log.e("TTSManager", "TTS initialization failed")
            }
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            isPaused = false
            spokenText = "" // Reset spoken text
            remainingText = text // Set the remaining text
            val firstChunk = extractNextChunk(0, text) // Get the first chunk
            textToSpeech?.speak(firstChunk, TextToSpeech.QUEUE_FLUSH, null, "ttsSpeakFirst")
        } else {
            Log.e("TTSManager", "TTS not initialized")
        }
    }

    fun pauseSpeaking() {
        if (isInitialized) {
            textToSpeech?.stop() // Stop TTS when paused
            isPaused = true
            remainingText = remainingText.substring(spokenText.length) // Update remaining text
            Log.d("TTSManager", "Paused. Remaining text: $remainingText")
        }
    }

    fun resumeSpeaking() {
        if (isInitialized && isPaused) {
            isPaused = false
            // Continue speaking the remaining text
            val nextChunk = extractNextChunk(spokenText.length, remainingText)
            textToSpeech?.speak(nextChunk, TextToSpeech.QUEUE_FLUSH, null, "ttsResume")
            Log.d("TTSManager", "Resumed speaking from: $nextChunk")
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        reset()
    }

    fun shutdown() {
        textToSpeech?.shutdown()
    }

    // Helper function to extract the next chunk of text (e.g., sentence or word)
    private fun extractNextChunk(startIndex: Int, text: String): String {
        // Break the text into sentences or words
        val remainingText = text.substring(startIndex).trim()
        val nextSentenceEnd = remainingText.indexOf('.')
        return if (nextSentenceEnd != -1) {
            remainingText.substring(0, nextSentenceEnd + 1).trim() // Return the next sentence
        } else {
            remainingText // Return whatever is left
        }
    }

    private fun reset() {
        spokenText = ""
        remainingText = ""
        isPaused = false
    }
}
