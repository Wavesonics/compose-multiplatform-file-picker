import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        var show by remember { mutableStateOf(false) }
        var fileName by remember { mutableStateOf("No file chosen") }
        Button(attrs = {
            onClick {
                show = true
            }
        }) {
            Text("Pick a file")
        }
        Br()
        Text("File path: $fileName")
        Br()
        Text("File content: TODO")

        FilePicker(show, fileExtensions = listOf(".txt", ".png")) { path ->
            fileName = path?.fileNameOrPath ?: "none selected"
            show = false
        }
    }
}