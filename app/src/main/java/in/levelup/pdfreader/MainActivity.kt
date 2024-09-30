package `in`.levelup.pdfreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import `in`.levelup.pdfreader.screen.PdfScreenViewModel
import `in`.levelup.pdfreader.screen.PdfViewerScreen
import `in`.levelup.pdfreader.ui.theme.LevelUPPdfReaderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LevelUPPdfReaderTheme {

                val viewmodel: PdfScreenViewModel = hiltViewModel()
                val state = viewmodel.state.value

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PdfViewerScreen(
                        modifier = Modifier
                            .padding(innerPadding),
                        states = state,
                        events = viewmodel::event
                    )
                }
            }

        }
    }
}