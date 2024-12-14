package `in`.levelup.pdfreader.screen.pdf_reader_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import `in`.levelup.Pdfreader.R

@Composable
fun PdfViewerScreen(
    modifier: Modifier = Modifier,
    states: PdfScreenStates,
    events: (PdfScreenEvents) -> Unit,
    navController: NavController,
    id: Int) {

    var pdfPageIndex by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        events(PdfScreenEvents.GetPdfTextById(pdfId = id))
    }

    Surface(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.background(Color.DarkGray),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center) {

            if (states.result.isNotEmpty()) {

                val pdfTextList = states.result.first()

                //PlayPauseRow

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(color = Color.DarkGray),
                ) {

                    PageNavigationIcon(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        icon = R.drawable.arrow_left_alt,
                        iconContentDescription = "go back",
                        onClick = {
                            navController.popBackStack()
                        }
                    )

                    PageNavigationIcon(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        icon = R.drawable.arrow_back,
                        iconContentDescription = "previous page",
                        onClick = {
                            events(
                                PdfScreenEvents.StopSpeaking
                            )
                            if (pdfPageIndex > 0) {
                                pdfPageIndex--
                            }
                        }
                    )

                    //play pause logic
                    if (!states.isSpeaking) {
                        PageNavigationIcon(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            icon = R.drawable.play_arrow,
                            iconContentDescription = "play",
                            onClick = {
                                if (states.isPaused) {
                                    events(
                                        PdfScreenEvents.ResumeSpeaking
                                    )
                                } else {
                                    events(
                                        PdfScreenEvents.SpeakText(
                                            pdfTextList.pdfTexts[pdfPageIndex].text
                                        )
                                    )
                                }
                            }
                        )
                    }
                    if (states.isSpeaking && !states.isPaused) {
                        PageNavigationIcon(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            icon = R.drawable.pause,
                            iconContentDescription = "pause",
                            onClick = {
                                events(PdfScreenEvents.PauseSpeaking)
                            }
                        )
                    }

                    PageNavigationIcon(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        icon = R.drawable.arrow_forward,
                        iconContentDescription = "next page",
                        onClick = {
                            events(
                                PdfScreenEvents.StopSpeaking
                            )
                            if (pdfTextList.pdfTexts.size > pdfPageIndex + 1) {
                                pdfPageIndex++
                            }
                        }
                    )

                    PageNavigationIcon(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        icon = R.drawable.replay,
                        iconContentDescription = "restart",
                        onClick = {
                            events(
                                PdfScreenEvents.SpeakText(
                                    pdfTextList.pdfTexts[pdfPageIndex].text
                                )
                            )
                        }
                    )

                    PageNavigationIcon(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        icon = R.drawable.fast_rewind,
                        iconContentDescription = "previous line",
                        onClick = {
                            events(
                                PdfScreenEvents.PreviousLive
                            )
                        }
                    )

                    PageNavigationIcon(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        icon = R.drawable.fast_forward,
                        iconContentDescription = "next line",
                        onClick = {
                            events(
                                PdfScreenEvents.SkipLine
                            )
                        }
                    )
                }

                InfoText(label = pdfTextList.pdf.pdfName)
                InfoText(label = "Page ${pdfPageIndex + 1}/${pdfTextList.pdfTexts.size}")

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = Color.Black)) {

                    Text(
                        modifier = Modifier
                            .padding(3.dp)
                            .verticalScroll(rememberScrollState()),
                        color = Color.White,
                        text = pdfTextList.pdfTexts[pdfPageIndex].text
                    )
                }

            } else {
                //TODO
            }
        }
    }
}

@Composable
@Preview
fun PdfViewerScreenPreview() {
    PdfViewerScreen(
        states = PdfScreenStates(
            loading = false,
            result = listOf()),
        events = {},
        id = 0,
        navController = rememberNavController()
    )
}

@Composable
fun PageNavigationIcon(
    modifier: Modifier,
    icon: Int,
    onClick: () -> Unit,
    iconContentDescription: String
){

    Box(modifier = modifier
        .padding(3.dp)
        .clip(shape = RoundedCornerShape(5))
        .background(color = Color.Gray),
        contentAlignment = Alignment.Center
        ) {
        IconButton(
            modifier = Modifier,
            onClick = onClick
        ) {
            Icon(
                painter = painterResource(id = icon),
                tint = Color.White,
                contentDescription = null
            )
        }
    }

}

@Composable
fun InfoText(label: String) {
    Text(
        modifier = Modifier.padding(start = 3.dp,
            top = 4.dp,
            bottom = 4.dp
        ),
        text = label,
        color = Color.White,
        fontWeight = FontWeight.SemiBold
    )
}