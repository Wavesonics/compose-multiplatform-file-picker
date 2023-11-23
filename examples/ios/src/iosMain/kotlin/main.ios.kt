import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.darkrockstudios.libraries.mpfilepicker.launchDirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.launchFilePicker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.UIKit.UIViewController

@Suppress("Unused", "FunctionName")
fun MainViewController(): UIViewController = ComposeUIViewController {
	val fileType = listOf("jpg", "png", "md")

	MaterialTheme {
		Column {
			var showSingleFilePicker by remember { mutableStateOf(false) }
			var singlePathChosen by remember { mutableStateOf("") }

			Button(onClick = {
				showSingleFilePicker = true
			}) {
				Text("Choose File")
			}
			Text("File Chosen: $singlePathChosen")

			FilePicker(showSingleFilePicker, fileExtensions = fileType) { mpFile ->
				singlePathChosen = mpFile?.path ?: "none selected"
				showSingleFilePicker = false
			}

			/////////////////////////////////////////////////////////////////

			var showMultipleFilePicker by remember { mutableStateOf(false) }
			var multiplePathChosen by remember { mutableStateOf(listOf("")) }

			Button(onClick = {
				showMultipleFilePicker = true
			}) {
				Text("Choose Multiple Files")
			}
			Text("Files Chosen: $multiplePathChosen")

			MultipleFilePicker(showMultipleFilePicker, fileExtensions = fileType) { mpFiles ->
				multiplePathChosen = mpFiles?.map { it.path } ?: emptyList()
				showMultipleFilePicker = false
			}

			/////////////////////////////////////////////////////////////////

			var nonComposeFileChosen by remember { mutableStateOf("") }

			Button(onClick = {
				MainScope().launch {
					nonComposeFileChosen = launchFilePicker(fileExtensions = fileType)
						.firstOrNull()?.path ?: "none selected"
				}
			}) {

				Text("Choose File Non-Compose")
			}
			Text("File Chosen: $nonComposeFileChosen")

			/////////////////////////////////////////////////////////////////

			var showDirPicker by remember { mutableStateOf(false) }
			var dirChosen by remember { mutableStateOf("") }

			Button(onClick = {
				showDirPicker = true
			}) {
				Text("Choose Directory")
			}
			Text("Directory Chosen: $dirChosen")

			DirectoryPicker(showDirPicker) { path ->
				dirChosen = path ?: "none selected"
				showDirPicker = false
			}

			/////////////////////////////////////////////////////////////////

			var nonComposeDirChosen by remember { mutableStateOf("") }

			Button(onClick = {
				MainScope().launch {
					nonComposeDirChosen = launchDirectoryPicker()
						.firstOrNull()?.path ?: "none selected"
				}
			}) {

				Text("Choose Directory Non-Compose")
			}
			Text("Directory Chosen: $nonComposeDirChosen")

		}
	}
}