import androidx.compose.runtime.*
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.WebFile
import com.darkrockstudios.libraries.mpfilepicker.readFileAsText
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        var show by remember { mutableStateOf(false) }
        var fileName by remember { mutableStateOf("No file chosen") }
        var fileContents by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
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
        Text("File content: $fileContents")

        FilePicker(show, fileExtensions = listOf("txt", "md")) { file ->
            if(file is WebFile) {
                fileName = file.path ?: "none selected"
                scope.launch {
                    fileContents = readFileAsText(file.platformFile)
                }
            }
        }
    }
}