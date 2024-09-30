package `in`.levelup.pdfreader.screen

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.levelup.pdfreader.repository.Repository
import `in`.levelup.pdfreader.util.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfScreenViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private val _state = mutableStateOf(PdfScreenStates())
    val state: State<PdfScreenStates> = _state

    init {

    }

    private fun extractTextFromPdfBitmaps(pdfBitmaps: List<Bitmap>) = viewModelScope.launch {
        repository.extractTextFromPdfBitmapsAsFlow(pdfBitmaps = pdfBitmaps).collect{ result ->
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

                }
            }
        }
    }

    private fun getTextFromScannedPdf(pdfBitmaps: List<Bitmap>) = viewModelScope.launch {

        repository.recognizeTextFromImages(pdfBitmaps = pdfBitmaps).collect{ result ->

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
                extractTextFromPdfBitmaps(event.pdfBitmaps)
            }
        }
    }
}