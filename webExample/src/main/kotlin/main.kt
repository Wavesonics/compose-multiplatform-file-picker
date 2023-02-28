import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        var show by remember { mutableStateOf(false) }
        Button(attrs = {
            onClick {
                show = true
            }
        }) {
            Text("Pick a file")
        }

        FilePicker(show, fileExtensions = listOf("jpg", "png")) { path ->
//            pathChosen = path ?: "none selected"
            show = false
        }
    }
}