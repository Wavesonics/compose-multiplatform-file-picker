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
import com.darkrockstudios.libraries.mpfilepicker.SaveFilePicker
import java.io.File

fun main() = application {
	var showSingleFile by remember { mutableStateOf(false) }
	var pathSingleChosen by remember { mutableStateOf("") }

	var showMultiFile by remember { mutableStateOf(false) }
	var pathMultiChosen by remember { mutableStateOf(listOf("")) }

	var showDirPicker by remember { mutableStateOf(false) }
	var dirChosen by remember { mutableStateOf("") }

	var showSaveFile by remember { mutableStateOf(false) }
	var hasSavedFiled by remember { mutableStateOf(false) }

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

			/////////////////////////////////////////////////////////////////


			Button(onClick = {
				showSaveFile = true
			}) {
				Text("Save File")
			}
			Text("Saved File: $hasSavedFiled")
		}
	}

	FilePicker(showSingleFile, fileExtensions = listOf("jpg", "png")) { file ->
		pathSingleChosen = file?.path ?: "none selected"
		showSingleFile = false
	}

	MultipleFilePicker(showMultiFile, fileExtensions = listOf("jpg", "png")) { files ->
		pathMultiChosen = files?.map { it.path + "\n" } ?: emptyList()
		showMultiFile = false
	}

	DirectoryPicker(showDirPicker) { path ->
		dirChosen = path ?: "none selected"
		showDirPicker = false
	}

	SaveFilePicker(
		showSaveFile,
		filename = "newFile.txt",
	) { selectedFile ->
		val contents = "this is a new test file"
		hasSavedFiled = selectedFile?.path?.let { path ->
			writeToFile(path, contents)
			true
		} ?: false
	}
}

private fun writeToFile(path: String, contents: String) {
	File(path).bufferedWriter().use { out ->
		out.write(contents)
	}
}
