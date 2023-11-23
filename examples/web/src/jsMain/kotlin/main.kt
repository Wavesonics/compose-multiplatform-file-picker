import androidx.compose.runtime.*
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.darkrockstudios.libraries.mpfilepicker.WebFile
import com.darkrockstudios.libraries.mpfilepicker.readFileAsByteArray
import com.darkrockstudios.libraries.mpfilepicker.readFileAsText
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
	renderComposable(rootElementId = "root") {
		val scope = rememberCoroutineScope()

		var showSingleFile by remember { mutableStateOf(false) }
		var fileName by remember { mutableStateOf("No file chosen") }
		var fileContents by remember { mutableStateOf("") }
		Button(attrs = {
			onClick {
				showSingleFile = true
			}
		}) {
			Text("Pick a file")
		}
		Br()
		Text("File path: $fileName")
		Br()
		Text("File content: $fileContents")

		FilePicker(showSingleFile, fileExtensions = listOf("txt", "md")) { file ->
			if (file is WebFile) {
				fileName = file.path
				scope.launch {
					fileContents = readFileAsText(file.platformFile)
				}
			}
			showSingleFile = false
		}

		var showMultipleFile by remember { mutableStateOf(false) }
		var filesNames by remember { mutableStateOf(listOf("")) }
		Button(attrs = {
			onClick {
				showMultipleFile = true
			}
		}) {
			Text("Pick multiple files")
		}
		Br()
		Text("File path: $fileName")
		Br()
		Text("File content: $fileContents")

		MultipleFilePicker(showMultipleFile, fileExtensions = listOf("txt", "md"), initialDirectory = null) { files ->
			files?.map { file ->
				if (file is WebFile) {
					filesNames += file.path + "\n"
					scope.launch {
						fileContents = readFileAsText(file.platformFile)
					}
				}
				showMultipleFile = false
			}
		}
	}
}