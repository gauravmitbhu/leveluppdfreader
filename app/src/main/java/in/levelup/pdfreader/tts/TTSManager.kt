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
    private var spokenText = "" // Track spoken text
    private var isPaused = false
    private val sentences: List<String>
        get() = remainingText.split(".").map { it.trim() }.filter { it.isNotEmpty() }
    private var currentSentenceIndex = 0

    // Initialize TTS and UtteranceProgressListener
    fun init() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.getDefault()
                isInitialized = true
                textToSpeech?.setOnUtteranceProgressListener(speechListener)
            } else {
                Log.e("TTSManager", "TTS initialization failed")
            }
        }
    }

    // Function to start speaking a sentence from a specified index
    private fun speakSentence(index: Int) {
        if (index in sentences.indices) {
            currentSentenceIndex = index
            spokenText = sentences.take(index).joinToString(". ") + ". "
            val sentenceToSpeak = sentences[index]
            textToSpeech?.speak(sentenceToSpeak, TextToSpeech.QUEUE_FLUSH, null, "ttsSpeak")
        } else {
            ttsListener?.onTTSFinished()
            reset()
        }
    }

    // Function to skip to the next sentence
    fun skipToNextSentence() {
        if (isInitialized) {
            if (currentSentenceIndex < sentences.size - 1) {
                speakSentence(currentSentenceIndex + 1)
            } else {
                Log.d("TTSManager", "Already at the last sentence")
                ttsListener?.onTTSFinished()
            }
        }
    }

    // Function to go back to the previous sentence
    fun goToPreviousSentence() {
        if (isInitialized && currentSentenceIndex > 0) {
            speakSentence(currentSentenceIndex - 1)
        } else {
            Log.d("TTSManager", "Already at the first sentence")
        }
    }

    // Start speaking from the beginning
    fun speak(text: String) {
        if (isInitialized) {
            isPaused = false
            spokenText = ""
            remainingText = text
            currentSentenceIndex = 0
            speakSentence(currentSentenceIndex)
        } else {
            Log.e("TTSManager", "TTS not initialized")
        }
    }

    // Pause, resume, and stop functions
    fun pauseSpeaking() {
        if (isInitialized) {
            textToSpeech?.stop()
            isPaused = true
        }
    }

    fun resumeSpeaking() {
        if (isInitialized && isPaused) {
            isPaused = false
            speakSentence(currentSentenceIndex)
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        reset()
    }

    fun shutdown() {
        textToSpeech?.shutdown()
    }

    // Reset function to clear data
    private fun reset() {
        spokenText = ""
        remainingText = ""
        currentSentenceIndex = 0
        isPaused = false
    }

    private val speechListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            Log.d("TTSManager", "onStart: Speaking started")
        }

        override fun onDone(utteranceId: String?) {
            spokenText += sentences[currentSentenceIndex] + ". "
            if (currentSentenceIndex < sentences.size - 1) {
                skipToNextSentence()
            } else {
                ttsListener?.onTTSFinished()
                reset()
            }
        }

        @Deprecated("Deprecated in Java", ReplaceWith(
            "Log.e(\"TTSManager\", \"onError: Error while speaking\")",
            "android.util.Log"))
        override fun onError(utteranceId: String?) {
            Log.e("TTSManager", "onError: Error while speaking")
        }
    }
}