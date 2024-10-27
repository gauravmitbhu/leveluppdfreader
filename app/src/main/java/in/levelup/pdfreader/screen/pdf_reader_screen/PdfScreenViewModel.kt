package `in`.levelup.pdfreader.screen.pdf_reader_screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.levelup.pdfreader.repository.Repository
import `in`.levelup.pdfreader.tts.TTSListener
import `in`.levelup.pdfreader.tts.TTSManager
import `in`.levelup.pdfreader.util.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfScreenViewModel @Inject constructor(private val repository: Repository,
                                             private val ttsManager: TTSManager): ViewModel(), TTSListener {

    private val _state = mutableStateOf(PdfScreenStates())
    val state: State<PdfScreenStates> = _state

    init {
        ttsManager.setTTSListener(this)
        if (!ttsManager.isInitialized){
            ttsManager.init()
        }
    }

    private fun getPdfTextWithId(id: Int) = viewModelScope.launch {
        repository.getPdfTextById(id).collect{ result ->
            when(result){
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        result = result.data!!
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun event(event: PdfScreenEvents){
        when(event){
            is PdfScreenEvents.GetPdfTextById -> {
                getPdfTextWithId(event.pdfId)
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
        }
    }

    //tts manager
    // Function to handle speaking text

    private fun ttsSpeak(text: String) {
        _state.value = _state.value.copy(
            isSpeaking = true,
            isPaused = false,
            currentText = text
        )
        ttsManager.speak(text)
    }

    // Function to pause TTS
    private fun pauseTts() {
        _state.value = _state.value.copy(
            isPaused = true,
            isSpeaking = false
        )
        ttsManager.pauseSpeaking()
    }

    // Function to resume TTS
    private fun resumeTts() {
        if (_state.value.isPaused) {
            _state.value = _state.value.copy(
                isPaused = false,
                isSpeaking = true
            )
            ttsManager.resumeSpeaking()
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

    override fun onCleared() {
        super.onCleared()
        ttsManager.isInitialized = false
        ttsManager.stopSpeaking()
        ttsManager.shutdown()
    }

    override fun onTTSFinished() {
        Log.d("TAG", "onTTSFinished: finished")
        _state.value = _state.value.copy(
            isPaused = false,
            isSpeaking = false
        )
    }

}