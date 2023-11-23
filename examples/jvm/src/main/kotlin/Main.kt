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
	var show by remember { mutableStateOf(false) }
	var pathChosen by remember { mutableStateOf(listOf("")) }

	var showDirPicker by remember { mutableStateOf(false) }
	var dirChosen by remember { mutableStateOf("") }

	Window(onCloseRequest = ::exitApplication) {
		Column {
			Button(onClick = {
				show = true
			}) {
				Text("Choose File")
			}
			Text("File Chosen: $pathChosen")

			/////////////////////////////////////////////////////////////////

			Button(onClick = {
				showDirPicker = true
			}) {
				Text("Choose Directory")
			}
			Text("Directory Chosen: $dirChosen")
		}
	}

	FilePicker(show, fileExtensions = listOf("jpg", "png")) { file ->
		pathChosen = listOf(file?.path ?: "none selected")
		show = false
	}

	MultipleFilePicker(show, fileExtensions = listOf("jpg", "png")) { files ->
		pathChosen = files?.map { it.path } ?: emptyList()
		show = false
	}

	DirectoryPicker(showDirPicker) { path ->
		dirChosen = path ?: "none selected"
		showDirPicker = false
	}
}
