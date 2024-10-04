package `in`.levelup.pdfreader.screen.pdf_reader_screen

import `in`.levelup.pdfreader.model.PdfText

data class PdfScreenStates(
    val loading: Boolean = false,
    val result: List<PdfText> = emptyList(),
    val isPaused: Boolean = false,
    val isSpeaking: Boolean = false,
    val currentText: String = "",
    val remainingText: String = ""
)