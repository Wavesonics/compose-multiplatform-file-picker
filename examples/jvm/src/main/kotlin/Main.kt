import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker

fun main() = application {
	var showSingleFile by remember { mutableStateOf(false) }
	var pathSingleChosen by remember { mutableStateOf("") }

	var showMultiFile by remember { mutableStateOf(false) }
	var pathMultiChosen by remember { mutableStateOf(listOf("")) }

	var showDirPicker by remember { mutableStateOf(false) }
	var dirChosen by remember { mutableStateOf("") }

	Window(onCloseRequest = ::exitApplication) {
		Column {
			Button(onClick = {
				showSingleFile = true
			}) {
				Text("Choose File")
			}
			Text("File Chosen: $pathSingleChosen")

			/////////////////////////////////////////////////////////////////

			Button(onClick = {
				showMultiFile = true
			}) {
				Text("Choose Multiple File")
			}
			Text("Files Chosen: $pathMultiChosen")

			/////////////////////////////////////////////////////////////////


			Button(onClick = {
				showDirPicker = true
			}) {
				Text("Choose Directory")
			}
			Text("Directory Chosen: $dirChosen")
		}
	}

	FilePicker(
		showSingleFile,
		fileExtensions = listOf("jpg", "png"),
		title = "Choose a file",
	) { file ->
		pathSingleChosen = file?.file?.path ?: "none selected"
		showSingleFile = false
	}

	MultipleFilePicker(
		showMultiFile,
		fileExtensions = listOf("jpg", "png"),
		title = "Choose files"
	) { files ->
		pathMultiChosen = files?.map { it.file.path + "\n" } ?: emptyList()
		showMultiFile = false
	}

	DirectoryPicker(
		showDirPicker,
		title = "Choose a directory"
	) { path ->
		dirChosen = path ?: "none selected"
		showDirPicker = false
	}
}
