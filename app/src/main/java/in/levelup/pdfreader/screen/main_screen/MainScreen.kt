package `in`.levelup.pdfreader.screen.main_screen

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.semantics.Role
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
    navController: NavController
) {
    var selectedPdfLanguage by remember { mutableStateOf("") }
    val shouldShowDialog = remember { mutableStateOf(false) }
    val shouldShowPdfSelectionDialog = remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val selectedPdfId = remember { mutableIntStateOf(0) }
    var selectedPdfTypeOption by remember { mutableStateOf("") }

    val pdfBitmapConverter = remember {
        PdfBitmapConverter(context)
    }

    var pdfUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var renderedPages by remember {
        mutableStateOf<List<Bitmap>>(emptyList())
    }

    val choosePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        pdfUri = uri
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // alert dialog
        @Composable
        fun DeleteEntryDialog(shouldShowDialog: MutableState<Boolean>) {
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

        @Composable
        fun PdfSelectionDialog() {
            Dialog(onDismissRequest = { shouldShowPdfSelectionDialog.value = false }) {
                RadioButtonSingleSelection { pdfSelectedOption ->
                    selectedPdfTypeOption = pdfSelectedOption
                    shouldShowPdfSelectionDialog.value = false
                    choosePdfLauncher.launch("application/pdf")
                }
            }
        }

        @Composable
        fun LanguageSelectionDialog(
            onDismiss: () -> Unit,
            onLanguageSelected: (String) -> Unit
        ) {
            var selectedLanguage by remember { mutableStateOf("") }

            Dialog(onDismissRequest = onDismiss) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Select Language",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        val languages = listOf("Chinese", "Devanagari", "Japanese", "Korean")
                        languages.forEach { language ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLanguage = language
                                        onLanguageSelected(language)
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedLanguage == language,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = language,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    if (selectedLanguage.isNotEmpty()) {
                                        onLanguageSelected(selectedLanguage)
                                    }
                                    shouldShowPdfSelectionDialog.value = false
                                    choosePdfLauncher.launch("application/pdf")
                                    onDismiss()
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }

        if (shouldShowDialog.value) {
            DeleteEntryDialog(shouldShowDialog = shouldShowDialog)
        }

        if (shouldShowPdfSelectionDialog.value) {
            LanguageSelectionDialog(
                onLanguageSelected = { language ->
                    selectedPdfLanguage = language
                },
                onDismiss = {
                    shouldShowPdfSelectionDialog.value = false
                }
            )
        }

        LaunchedEffect(pdfUri) {
            pdfUri?.let { uri ->
                states.loading = true
                renderedPages = pdfBitmapConverter.pdfToBitmaps(uri) // causes delay in loading
                events(
                    MainScreenEvents.InsertScannedPdf(
                        pdf = Pdf(pdfName = getFileNameFromUri(uri = uri, context = context)),
                        bitmaps = renderedPages,
                        language = selectedPdfLanguage
                    )
                )
            }
        }

        if (!states.loading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(width = 2.dp, color = Color.White),
                    shape = RoundedCornerShape(15),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = {
                        shouldShowPdfSelectionDialog.value = true
                    }
                ) {
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
            // loading dialog
            Dialog(
                onDismissRequest = { showLoadingDialog = false },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
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
fun MainScreenPreview() {
    MainScreen(
        states = MainScreenStates(),
        events = {},
        navController = rememberNavController()
    )
}

@Composable
fun RadioButtonSingleSelection(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val radioOptions = listOf("Text Pdf", "Scanned Pdf")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[1]) }
    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Column(
        modifier
            .selectableGroup()
            .border(width = 2.dp, color = Color.White)
    ) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(width = 2.dp, color = Color.White),
            shape = RoundedCornerShape(15),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            onClick = { onClick(selectedOption) }
        ) {
            Text("Select")
        }
    }
}

@Composable
fun PdfLazyList(
    pdfs: Pdf,
    navController: NavController,
    shouldShowDialog: MutableState<Boolean>,
    selectedPdfId: MutableState<Int>
) {
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
        Text(
            pdfs.pdfName,
            maxLines = 1,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(horizontal = 10.dp),
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        )
    }
}
