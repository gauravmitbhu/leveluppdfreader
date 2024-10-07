package `in`.levelup.pdfreader.screen.pdf_reader_screen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.levelup.pdfreader.repository.Repository
import `in`.levelup.pdfreader.tts.TTSManager
import `in`.levelup.pdfreader.util.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfScreenViewModel @Inject constructor(private val repository: Repository,
                                             private val ttsManager: TTSManager
): ViewModel() {

    private val _state = mutableStateOf(PdfScreenStates())
    val state: State<PdfScreenStates> = _state

    init {
        ttsManager.init()
    }

    private fun extractTextFromPdfBitmaps(context: Context,
                                          uri: Uri
    ) = viewModelScope.launch {
        repository.extractTextFromPdfUriAsFlow(context = context,
            pdfUri = uri).collect{ result ->
            when(result){
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        //result = result.data!!
                    )
                }
                is Resource.Error -> {
                    Log.d("TAG", "extractTextFromPdfBitmaps: error ${result.message}")
                }
            }
        }
    }

   /* fun fetchPdfTextById(pdfId: Int) {
        viewModelScope.launch {
            repository.getPdfTextById(pdfId).collect { pdfTexts ->
                _state.value = _state.value.copy(
                    result = pdfTexts
                )
            }
        }
    }*/

    private fun getTextFromScannedPdf(pdfBitmaps: List<Bitmap>) = viewModelScope.launch {

        repository.recognizeTextFromImages(
            id = 1,
            pdfBitmaps = pdfBitmaps).collect{ result ->

            when(result){
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        loading = false,
                    )
                }
                is Resource.Error -> {
                    TODO()
                }
            }
        }
    }

    fun event(event: PdfScreenEvents){
        when(event){
            is PdfScreenEvents.GetTextFromScannedPdf -> {
                getTextFromScannedPdf(event.pdfBitmaps)
            }
            is PdfScreenEvents.ExtractTextFromPdfBitmaps -> {
                extractTextFromPdfBitmaps(context = event.context,
                    uri = event.uri)
            }
            is PdfScreenEvents.PauseSpeaking -> {
                pauseTts()
            }
            is PdfScreenEvents.ResumeSpeaking -> {
                resumeTts()
            }
            is PdfScreenEvents.SpeakText -> {
                ttsSpeak(event.text)
            }
            is PdfScreenEvents.StopSpeaking -> {
                stopTTs()
            }

            is PdfScreenEvents.GetPdfById -> {
              //  fetchPdfTextById(event.pdfId)
            }
        }
    }

    //tts manager

    // Function to handle speaking text
    private fun ttsSpeak(text: String) {
        _state.value = _state.value.copy(
            isSpeaking = true,
            isPaused = false,
            currentText = text // Keep track of the current text
        )
        ttsManager.speak(text)
        Log.d("TAG", "ttsSpeak: Speak executed")
    }

    // Function to pause TTS
    private fun pauseTts() {
        _state.value = _state.value.copy(
            isPaused = true,
            isSpeaking = false
        )
        ttsManager.pauseSpeaking()
        Log.d("TAG", "ttsSpeak: pause executed")
    }

    // Function to resume TTS
    private fun resumeTts() {
        if (_state.value.isPaused) {
            _state.value = _state.value.copy(
                isPaused = false,
                isSpeaking = true
            )
            ttsManager.resumeSpeaking()
            Log.d("TAG", "ttsSpeak: resume executed")
        }
    }

    // Function to stop TTS
    private fun stopTTs() {
        ttsManager.stopSpeaking()
        _state.value = _state.value.copy(
            isSpeaking = false,
            isPaused = false,
            remainingText = ""
        )
    }

}