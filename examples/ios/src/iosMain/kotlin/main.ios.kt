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
import com.darkrockstudios.libraries.mpfilepicker.SaveFilePicker
import com.darkrockstudios.libraries.mpfilepicker.launchDirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.launchFilePicker
import com.darkrockstudios.libraries.mpfilepicker.launchSaveFilePicker
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.Foundation.*
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

			FilePicker(showSingleFilePicker, fileExtensions = fileType) { platformFile ->
				singlePathChosen = platformFile?.nsUrl?.path ?: "none selected"
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

			MultipleFilePicker(showMultipleFilePicker, fileExtensions = fileType) { platformFiles ->
				multiplePathChosen = platformFiles?.map { it.nsUrl.path + "\n" } ?: emptyList()
				showMultipleFilePicker = false
			}

			/////////////////////////////////////////////////////////////////

			var nonComposeFileChosen by remember { mutableStateOf("") }

			Button(onClick = {
				MainScope().launch {
					nonComposeFileChosen = launchFilePicker(fileExtensions = fileType)
						.firstOrNull()?.nsUrl?.path ?: "none selected"
				}
			}) {

				Text("Choose File Non-Compose")
			}
			Text("File Chosen: $nonComposeFileChosen")

			/////////////////////////////////////////////////////////////////

			var nonComposeMultipleFileChosen by remember { mutableStateOf(listOf("")) }

			Button(onClick = {
				MainScope().launch {
					nonComposeMultipleFileChosen =
						launchFilePicker(fileExtensions = fileType, allowMultiple = true)
							.map { it.nsUrl.path + "\n" }
				}
			}) {

				Text("Choose Multiple Files Non-Compose")
			}
			Text("Multiple File Chosen: $nonComposeMultipleFileChosen")

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
						.firstOrNull()?.nsUrl?.path ?: "none selected"
				}
			}) {

				Text("Choose Directory Non-Compose")
			}
			Text("Directory Chosen: $nonComposeDirChosen")

			/////////////////////////////////////////////////////////////////

			var showSaveFilePicker by remember { mutableStateOf(false) }
			var savedFile by remember { mutableStateOf(false) }

			Button(onClick = {
				showSaveFilePicker = true
			}) {
				Text("Choose Save")
			}
			Text("Save File Chosen: $savedFile")

			SaveFilePicker(
				showSaveFilePicker,
				filename = "newFileName.txt",
			) { selectedFile ->
				val contents = "Slick saving tech"
				savedFile = selectedFile?.nsUrl?.path?.let { path ->
					writeToFile(path, contents).getOrNull() == true
				} ?: false
				showSaveFilePicker = false
			}

			/////////////////////////////////////////////////////////////////

			var nonComposeSaveFileChosen by remember { mutableStateOf(false) }

			Button(onClick = {
				MainScope().launch {
					nonComposeSaveFileChosen = launchSaveFilePicker(
						filename = "newNcFileName.txt",
					)?.let { selectedFile ->
						val contents = "Slick saving tech"
						selectedFile.nsUrl.path?.let { path ->
							writeToFile(path, contents).getOrNull() == true
						}
					} ?: false
				}
			}) {
				Text("Choose Save Non-Compose")
			}
			Text("Save File Chosen: $nonComposeSaveFileChosen")
		}
	}
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun writeToFile(path: String, contents: String): Result<Boolean> = runCatching {
	val contentsAsNsData = memScoped {
		NSString
			.create(string = contents)
			.dataUsingEncoding(NSUTF8StringEncoding)
	} ?: throw Throwable("contents should only include UTF8 values")
	contentsAsNsData.writeToFile(path, atomically = true)
}
