package `in`.levelup.pdfreader.screen.pdf_reader_screen

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.levelup.pdfreader.util.PdfBitmapConverter

@Composable
fun PdfViewerScreen(
    modifier: Modifier = Modifier,
    states: PdfScreenStates,
    events: (PdfScreenEvents) -> Unit,
    id: Int
) {

    Log.d("TAG", "PdfViewerScreen: $id")

    val context = LocalContext.current

    val pdfBitmapConverter = remember {
        PdfBitmapConverter(context)
    }

    var pdfUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var renderedPages by remember {
        mutableStateOf<List<Bitmap>>(emptyList())
    }

    var pdfPageIndex by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        events(PdfScreenEvents.GetPdfById(id))
    }

    LaunchedEffect(pdfUri) {
        pdfUri?.let { uri ->
            renderedPages = pdfBitmapConverter.pdfToBitmaps(uri)
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            Row(modifier = Modifier.fillMaxWidth()) {

                if (!states.isSpeaking) {
                    IconButton(onClick = {
                        if (states.isPaused) {
                            events(
                                PdfScreenEvents.ResumeSpeaking
                            )
                        }else {
                            events(
                                PdfScreenEvents.SpeakText(
                                    states.result[pdfPageIndex].text
                                )
                            )
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                    }
                }
                if(states.isSpeaking && !states.isPaused) {
                    IconButton(onClick = {
                        events(PdfScreenEvents.PauseSpeaking)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null
                        )
                    }
                }
            }

            if (!states.loading) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    text = states.result[pdfPageIndex].text
                )
            }else {
                CircularProgressIndicator()
            }

            Row(modifier = Modifier
                .height(60.dp)

                .padding(horizontal = 2.dp)
                .background(color = Color.Transparent)
                .fillMaxWidth()) {

                Spacer(modifier = Modifier.width(10.dp))

                Button(onClick = {
                    pdfPageIndex++
                }) {
                    Text("next page")
                }

                Spacer(modifier = Modifier.width(10.dp))

            }
        }
    }
}

@Composable
@Preview
fun PdfViewerScreenPreview() {
    PdfViewerScreen(
        states = PdfScreenStates(loading = true,
            result = listOf()),
        events = {},
        id = 0
    )
}