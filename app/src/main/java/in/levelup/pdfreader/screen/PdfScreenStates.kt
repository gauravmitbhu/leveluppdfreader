package `in`.levelup.pdfreader.screen

import android.graphics.Bitmap

data class PdfScreenStates (
    val loading: Boolean = false,
    val result: List<String> = listOf("")
)