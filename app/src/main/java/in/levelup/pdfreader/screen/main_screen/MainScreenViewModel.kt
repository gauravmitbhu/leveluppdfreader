package `in`.levelup.pdfreader.screen.main_screen

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.levelup.pdfreader.model.PdfText
import `in`.levelup.pdfreader.repository.Repository
import `in`.levelup.pdfreader.util.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private val _state = mutableStateOf(MainScreenStates())
    val state: State<MainScreenStates> = _state

    init {
        getAllPdfs()
    }

    private fun getAllPdfs() = viewModelScope.launch {
        repository.getAllDuration().collect{ result ->
            _state.value = _state.value.copy(
               pdfText = result
            )
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
                        isSuccess = true
                    )
                }
                is Resource.Error -> {
                    TODO()
                }
            }
        }
    }

    fun event(event: MainScreenEvents){
        when(event){
            is MainScreenEvents.ExtractTextFromPdfBitmaps -> {

            }
            is MainScreenEvents.GetTextFromScannedPdf -> {
                getTextFromScannedPdf(event.pdfBitmaps)
            }
            is MainScreenEvents.AddPdf -> {
    //            addPdf(event.pdfText)
            }
        }
    }

}