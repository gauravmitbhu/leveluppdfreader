package `in`.levelup.pdfreader.screen.main_screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.levelup.pdfreader.model.Pdf
import `in`.levelup.pdfreader.repository.Repository
import `in`.levelup.pdfreader.util.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    private val _state = mutableStateOf(MainScreenStates())
    val state: State<MainScreenStates> = _state

    init {
        viewModelScope.launch {
            repository.getAllPdf().collect{ result ->
                _state.value = _state.value.copy(
                    pdf = result
                )
            }
        }
    }

    private fun storePdfTextWithId(bitmaps: List<Bitmap>) = viewModelScope.launch {
        repository.getLatestPdfEntry().collect{ result ->
            when(result){
                is Resource.Loading -> {
                    Log.d("TAG", "getLatestEntry: Loading")
                }
                is Resource.Success -> {
                    Log.d("TAG", "getLatestEntry: ${result.data!!.pdfId}")
                    getTextFromPdfBitMaps(id = result.data.pdfId,
                        bitmaps = bitmaps
                        )
                }
                is Resource.Error -> {
                    Log.d("TAG", "getLatestEntry: ${result.message}")
                }
            }
        }
    }

    private fun getTextFromPdfBitMaps(
        id: Int,
        bitmaps: List<Bitmap>) = viewModelScope.launch {
        repository.recognizeTextFromImages(
            id = id,
            pdfBitmaps = bitmaps).collect{ result ->

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
                    _state.value = _state.value.copy(
                        loading = false,
                        isSuccess = false
                    )
                }
            }
        }
    }

    //room database
    private fun getAllPdf() = viewModelScope.launch {
        repository.getAllPdf().collect { result ->
            _state.value = _state.value.copy(
                pdf = result
            )
        }
    }

    private fun insertPdf(pdf: Pdf,
                          bitmaps: List<Bitmap>
                          ) = viewModelScope.launch {
        repository.insertPdf(pdf = pdf).collect{result ->
            when(result){
                is Resource.Loading -> {
                    Log.d("TAG", "insertPdf: Loading")
                }
                is Resource.Success -> {
                    storePdfTextWithId(bitmaps = bitmaps)
                }
                is Resource.Error -> {
                    Log.d("TAG", "insertPdf: Success")
                }
            }
        }
    }

    fun event(event: MainScreenEvents){
         when(event){
             is MainScreenEvents.GetTextFromPdfBitMaps -> {
                 //getTextFromPdfBitMaps(event.bitmaps)
             }
             is MainScreenEvents.AddPdf -> {
                 insertPdf(bitmaps = event.bitmaps,
                     pdf = event.pdf
                     )
             }
             is MainScreenEvents.GetAllPds -> {
                 getAllPdf()
             }
             is MainScreenEvents.StorePdfTextWithId -> {
                 storePdfTextWithId(event.bitmaps)
             }
         }
    }

}