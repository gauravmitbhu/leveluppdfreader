package `in`.levelup.pdfreader.screen

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import `in`.levelup.pdfreader.util.PdfBitmapConverter


@Composable
fun PdfViewerScreen(
    modifier: Modifier = Modifier,
    states: PdfScreenStates,
    events: (PdfScreenEvents) -> Unit
) {
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

    var renderedPageIndex by remember {
        mutableIntStateOf(0)
    }

    val extractedText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(pdfUri) {
        pdfUri?.let { uri ->
            renderedPages = pdfBitmapConverter.pdfToBitmaps(uri)
        }
    }

    val choosePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        pdfUri = it
    }

    Surface(modifier = modifier.fillMaxSize()
    ) {

        Column {

            if (!states.loading) {
                Text(
                    modifier = Modifier.height(300.dp),
                    text = states.result[0]

                )
            }else {
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier
                .height(10.dp)
                .weight(1f)
            )

            Row {

                Button(onClick = {
                    choosePdfLauncher.launch("application/pdf")
                }) {
                    Text("choose pdf")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(onClick = {
                    renderedPageIndex++
                }) {
                    Text("next page")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(onClick = {
                    if (renderedPages.isNotEmpty()){
                        events(PdfScreenEvents.ExtractTextFromPdfBitmaps(renderedPages))
                    }
                }) {
                    Text("convert to text")
                }
            }
        }
    }

}

@Composable
fun PdfPage(
    page: Bitmap,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = page,
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(page.width.toFloat() / page.height.toFloat())
            .drawWithContent {
                drawContent()
            }
    )
}

@Composable
@Preview
fun PdfViewerScreenPreview() {
    PdfViewerScreen(
        states = PdfScreenStates(loading = false, result = listOf()),
        events = {}
    )
}
