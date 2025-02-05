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
class MainScreenViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _state = mutableStateOf(MainScreenStates())
    val state: State<MainScreenStates> = _state

    init {
        viewModelScope.launch {
            repository.getAllPdf().collect { result ->
                _state.value = _state.value.copy(
                    pdf = result
                )
            }
        }
    }

    private fun storeScannedPdfTextWithId(
        bitmaps: List<Bitmap>,
        language: String
    ) = viewModelScope.launch {
        repository.getLatestPdfEntry().collect { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                }
                is Resource.Success -> {
                    Log.d("TAG", "getLatestEntry: ${result.data!!.pdfId}")
                    _state.value = _state.value.copy(
                        loading = false
                    )
                    getTextFromScannedPdfBitMaps(
                        id = result.data.pdfId,
                        bitmaps = bitmaps,
                        language = language
                    )
                }
                is Resource.Error -> {
                    Log.d("TAG", "getLatestEntry: ${result.message}")
                }
            }
        }
    }

    /*private fun storeExtractedPdfTextWithId(
        context: Context,
        pdfUri: Uri
    ) = viewModelScope.launch {
        repository.getLatestPdfEntry().collect { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                }
                is Resource.Success -> {
                    Log.d("TAG", "getLatestEntry: ${result.data!!.pdfId}")
                    _state.value = _state.value.copy(
                        loading = false
                    )
                    extractTextFromPdfUri(
                        id = result.data.pdfId,
                        context = context,
                        pdfUri = pdfUri,
                    )
                }
                is Resource.Error -> {
                    Log.d("TAG", "getLatestEntry: ${result.message}")
                }
            }
        }
    }*/

    private fun getTextFromScannedPdfBitMaps(
        id: Int,
        bitmaps: List<Bitmap>,
        language: String
    ) = viewModelScope.launch {
        repository.recognizeTextFromImages(
            id = id,
            pdfBitmaps = bitmaps,
            language = language
        ).collect { result ->

            when (result) {
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

    /*private fun extractTextFromPdfUri(id: Int, context: Context, pdfUri: Uri) = viewModelScope.launch {
        repository.extractTextFromPdfUriAsFlow(id = id, context = context, pdfUri = pdfUri).collect { result ->
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
    }*/

    // room database
    private fun getAllPdf() = viewModelScope.launch {
        repository.getAllPdf().collect { result ->
            _state.value = _state.value.copy(
                pdf = result
            )
        }
    }

    private fun deletePdfById(id: Int) = viewModelScope.launch {
        repository.deletePdfById(pdfId = id).collect { result ->
            when (result) {
                is Resource.Loading -> {}
                is Resource.Success -> {}
                is Resource.Error -> {}
            }
        }
    }

    private fun insertScannedPdf(
        pdf: Pdf,
        bitmaps: List<Bitmap>,
        language: String
    ) = viewModelScope.launch {
        repository.insertPdf(pdf = pdf).collect { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        loading = false
                    )
                    storeScannedPdfTextWithId(
                        bitmaps = bitmaps,
                        language = language
                    )
                }
                is Resource.Error -> {
                    Log.d("TAG", "insertPdf: Success")
                }
            }
        }
    }

    /*private fun insertExtractedPdf(
        pdf: Pdf,
        context: Context,
        pdfUri: Uri
    ) = viewModelScope.launch {
        repository.insertPdf(pdf = pdf).collect { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        loading = false
                    )
                    storeExtractedPdfTextWithId(
                        context = context,
                        pdfUri = pdfUri
                    )
                }
                is Resource.Error -> {
                    Log.d("TAG", "insertPdf: Success")
                }
            }
        }
    }*/

    // events

    fun event(event: MainScreenEvents) {
        when (event) {
            is MainScreenEvents.GetAllPds -> {
                getAllPdf()
            }
            is MainScreenEvents.InsertScannedPdf -> {
                insertScannedPdf(
                    bitmaps = event.bitmaps,
                    pdf = event.pdf,
                    language = event.language
                )
            }
            /*is MainScreenEvents.InsertExtractedPdf -> {
                insertExtractedPdf(
                    pdf = event.pdf,
                    context = event.context,
                    pdfUri = event.pdfUri
                )
            }
            is MainScreenEvents.StoreExtractedPdfTextWithId -> {
                storeExtractedPdfTextWithId(
                    context = event.context,
                    pdfUri = event.pdfUri
                )
            }*/
            is MainScreenEvents.StoreScannedPdfTextWithId -> {
                storeScannedPdfTextWithId(
                    bitmaps = event.bitmaps,
                    language = event.language
                )
            }
            is MainScreenEvents.DeletePdfById -> {
                deletePdfById(id = event.id)
            }
        }
    }
}
