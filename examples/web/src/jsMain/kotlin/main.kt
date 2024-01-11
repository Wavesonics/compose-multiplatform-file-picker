import androidx.compose.runtime.*
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.darkrockstudios.libraries.mpfilepicker.SaveFilePicker
import com.darkrockstudios.libraries.mpfilepicker.WebFile
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
			Text("Pick a text file")
		}
		Br()
		Text("File name: $fileName")
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

		Br()
		Br()
		Br()
		Br()

		var showMultipleFile by remember { mutableStateOf(false) }
		var filesNames by remember { mutableStateOf(emptyList<String>()) }
		Button(attrs = {
			onClick {
				showMultipleFile = true
			}
		}) {
			Text("Pick multiple image files")
		}
		Br()
		Text("Files names: $filesNames")
		Br()
		MultipleFilePicker(
			showMultipleFile,
			fileExtensions = listOf("png", "jpeg", "jpg"),
			initialDirectory = null
		) { files ->
			filesNames = files?.map { it.path } ?: listOf()
			showMultipleFile = false
		}
	}
}