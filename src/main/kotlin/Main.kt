import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.database.AppDatabase
import presentation.screens.MainScreen
import data.DriverFactory

@Composable
@Preview
fun StudentApp() {
    val db = AppDatabase(DriverFactory().createDriver())

    MainScreen(
        db = db,
        modifier = Modifier.fillMaxSize()
    )
}

fun main() = application {
    Window(
        title = "StudentApp",
        onCloseRequest = ::exitApplication
    ) {
        MaterialTheme {
            StudentApp()
        }
    }
}
