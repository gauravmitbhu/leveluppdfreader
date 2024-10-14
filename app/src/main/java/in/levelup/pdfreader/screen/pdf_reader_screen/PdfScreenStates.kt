package `in`.levelup.pdfreader.screen.pdf_reader_screen

import `in`.levelup.pdfreader.model.PdfText
import `in`.levelup.pdfreader.model.PdfsWithText

data class PdfScreenStates(
    val loading: Boolean = false,
    val error: String? = null,
    val result: List<PdfsWithText> = emptyList(),
    val isPaused: Boolean = false,
    val isSpeaking: Boolean = false,
    val currentText: String = "",
    val remainingText: String = ""
)