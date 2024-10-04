package `in`.levelup.pdfreader.screen.main_screen

import `in`.levelup.pdfreader.model.PdfText

data class MainScreenStates(
    val loading: Boolean = false,
    val pdfText: List<PdfText> = emptyList(),
    val isSuccess: Boolean = false,
    val isPaused: Boolean = false,
    val isSpeaking: Boolean = false,
    val currentText: String = "",
    val remainingText: String = ""
)