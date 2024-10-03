package `in`.levelup.pdfreader.screen

data class PdfScreenStates(
    val loading: Boolean = false,
    val result: List<String> = listOf(""),
    val isPaused: Boolean = false,
    val isSpeaking: Boolean = false,
    val currentText: String = "",
    val remainingText: String = ""
)