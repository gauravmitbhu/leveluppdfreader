package `in`.levelup.pdfreader.navigation

sealed class NavigationRoutes(
    val route: String
) {
    data object MainScreen: NavigationRoutes(route = "main_screen")
    data object PdfReaderScreen: NavigationRoutes(route = "pdf_reader_screen/{id}")
}