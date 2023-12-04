
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
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

		FilePicker(show, fileExtensions = listOf("txt", "md")) { platformFile ->
			platformFile?.let {
				scope.launch {
					fileContents = readFileAsText(platformFile.file)
				}
			}
			fileName = platformFile?.file?.name ?: "none selected"
			show = false
		}
	}
}