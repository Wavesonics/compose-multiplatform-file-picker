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
import com.darkrockstudios.libraries.mpfilepicker.launchDirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.launchFilePicker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.UIKit.UIViewController

@Suppress("Unused", "FunctionName")
fun MainViewController(): UIViewController = ComposeUIViewController {
	MaterialTheme {
		Column {
			var showFilePicker by remember { mutableStateOf(false) }
			var pathChosen by remember { mutableStateOf("") }

			Button(onClick = {
				showFilePicker = true
			}) {
				Text("Choose File")
			}
			Text("File Chosen: $pathChosen")

			val fileType = listOf("jpg", "png", "md")
			FilePicker(showFilePicker, fileExtensions = fileType) { mpFile ->
				pathChosen = mpFile?.path ?: "none selected"
				showFilePicker = false
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