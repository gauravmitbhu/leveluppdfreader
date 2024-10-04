package `in`.levelup.pdfreader.navigation

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import `in`.levelup.pdfreader.screen.main_screen.MainScreen
import `in`.levelup.pdfreader.screen.main_screen.MainScreenViewModel
import `in`.levelup.pdfreader.screen.pdf_reader_screen.PdfScreenStates
import `in`.levelup.pdfreader.screen.pdf_reader_screen.PdfViewerScreen


@Composable
fun MyNavigation(
    modifier: Modifier,
    navController: NavHostController,
) {
    NavHost(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = NavigationRoutes.MainScreen.route
    ) {

        composable(route = NavigationRoutes.MainScreen.route) {
            val viewModel: MainScreenViewModel = hiltViewModel()
            val states = viewModel.state.value
            MainScreen(
                states = states,
                events = viewModel::event,
                navController = navController
            )
        }

        composable(
            route = NavigationRoutes.PdfReaderScreen.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            PdfViewerScreen(
                states = PdfScreenStates(),
                events = {},
                id = id
            )
        }
    }
}
