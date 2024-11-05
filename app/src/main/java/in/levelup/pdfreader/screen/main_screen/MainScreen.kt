package `in`.levelup.pdfreader.screen.main_screen

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import `in`.levelup.pdfreader.model.Pdf
import `in`.levelup.pdfreader.util.PdfBitmapConverter
import `in`.levelup.pdfreader.util.getFileNameFromUri

@Composable
fun MainScreen(
    states: MainScreenStates,
    events: (MainScreenEvents) -> Unit,
    navController: NavController
){
    Surface(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
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

        LaunchedEffect(pdfUri) {
            pdfUri?.let { uri ->
                renderedPages = pdfBitmapConverter.pdfToBitmaps(uri)
                events(MainScreenEvents.AddPdf(
                    pdf = Pdf(pdfName = getFileNameFromUri(uri = uri, context = context)),
                    bitmaps = renderedPages
                ))
                events(MainScreenEvents.GetTextFromPdfBitMaps(renderedPages))
            }
        }

        val choosePdfLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            pdfUri = uri
        }

        if(!states.loading){
            Column(modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(15),
                    onClick = {
                        choosePdfLauncher.launch("application/pdf")
                    }) {
                    Text("Select Pdf")
                }

                LazyColumn {
                    items(states.pdf) { result ->
                        PdfLazyList(
                            pdfs = result,
                            navController = navController
                        )
                    }
                }
            }
        } else {
            Box(contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
        }

    }
}

@Composable
@Preview
fun MainScreenPreview(){
    MainScreen(
        states = MainScreenStates(),
        events = {},
        navController = rememberNavController()
    )
}

@Composable
fun PdfLazyList(pdfs: Pdf,
                navController: NavController
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .background(color = Color.White),
        verticalArrangement = Arrangement.Center
    ) {

        Text(pdfs.pdfName,
            maxLines = 1,
            color = Color.Black,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .clickable {
                    navController.navigate("pdf_reader_screen/${pdfs.pdfId}")
                },
            overflow = TextOverflow.Ellipsis
        )
    }
}