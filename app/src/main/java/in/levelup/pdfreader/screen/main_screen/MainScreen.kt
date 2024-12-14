package `in`.levelup.pdfreader.screen.main_screen

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import `in`.levelup.pdfreader.model.Pdf
import `in`.levelup.pdfreader.util.PdfBitmapConverter
import `in`.levelup.pdfreader.util.getFileNameFromUri

@Composable
fun MainScreen(
    states: MainScreenStates,
    events: (MainScreenEvents) -> Unit,
    navController: NavController){

    val shouldShowDialog = remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }

    val selectedPdfId = remember { mutableIntStateOf(0) }

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

    Surface(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

        //alert dialog
        @Composable
        fun MyAlertDialog(shouldShowDialog: MutableState<Boolean>) {
            if (shouldShowDialog.value) {
                AlertDialog(
                    icon = {
                        Icon(Icons.Default.Info, contentDescription = "dialog info")
                    },
                    onDismissRequest = {
                        shouldShowDialog.value = false
                    },
                    title = { Text(text = "Delete Entry") },
                    text = { Text(text = "Delete this pdf entry") },
                    confirmButton = {
                        Button(
                            onClick = {

                                events(
                                    MainScreenEvents.DeletePdfById(
                                        id = selectedPdfId.intValue
                                    )
                                )
                                shouldShowDialog.value = false
                            }
                        ) {
                            Text(
                                text = "Confirm",
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                shouldShowDialog.value = false
                            }
                        ) {
                            Text("Dismiss")
                        }
                    }

                )
            }
        }

        if (shouldShowDialog.value) {
            MyAlertDialog(shouldShowDialog = shouldShowDialog)
        }

        LaunchedEffect(pdfUri) {
            pdfUri?.let { uri ->
                renderedPages = pdfBitmapConverter.pdfToBitmaps(uri)
                events(MainScreenEvents.AddPdf(
                    pdf = Pdf(pdfName = getFileNameFromUri(uri = uri, context = context)),
                    bitmaps = renderedPages
                ))
                states.loading = false
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
                .background(color = Color.Black)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(width = 2.dp, color = Color.White),
                    shape = RoundedCornerShape(15),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black,
                        contentColor = Color.White),
                    onClick = {
                        choosePdfLauncher.launch("application/pdf")
                        states.loading = true
                    }) {
                    Text("Select Pdf")
                }

                LazyColumn {
                    items(states.pdf) { result ->
                        PdfLazyList(
                            pdfs = result,
                            navController = navController,
                            shouldShowDialog = shouldShowDialog,
                            selectedPdfId = selectedPdfId
                        )
                    }
                }
            }
        } else {
            Log.d("TAG", "MainScreen: loading")
            //loading dialog
            Dialog(
                onDismissRequest = { showLoadingDialog = false },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment= Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                ) {
                    CircularProgressIndicator(
                        color = Color.Black
                    )
                }
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
                navController: NavController,
                shouldShowDialog: MutableState<Boolean>,
                selectedPdfId: MutableState<Int>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        selectedPdfId.value = pdfs.pdfId
                        Log.d("TAG", "PdfLazyList: ${selectedPdfId.value}")
                        shouldShowDialog.value = true
                    },
                    onTap = {
                        navController.navigate("pdf_reader_screen/${pdfs.pdfId}")
                    }
                )
            }
            .background(color = Color.Black),
        verticalArrangement = Arrangement.Center,
    ) {

        Text(pdfs.pdfName,
            maxLines = 1,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(horizontal = 10.dp),
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        HorizontalDivider(modifier = Modifier.fillMaxWidth(),
            color = Color.White
        )
    }
}