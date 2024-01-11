import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.darkrockstudios.libraries.mpfilepicker.SaveFilePicker
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.AppKit.NSApp
import platform.AppKit.NSApplication
import platform.Foundation.NSError
import platform.Foundation.NSFileHandle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.closeFile
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.fileHandleForWritingAtPath

fun main() {
	NSApplication.sharedApplication()
	Window(title = "Youtube history") {
		MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
			Scaffold {
				var showSingleFile by remember { mutableStateOf(false) }
				var showMultipleFile by remember { mutableStateOf(false) }
				var singleFilePathChosen by remember { mutableStateOf("") }
				var multipleFilesPathsChosen by remember { mutableStateOf(listOf("")) }

				var showDirPicker by remember { mutableStateOf(false) }
				var dirChosen by remember { mutableStateOf("") }

				var showSaveFilePicker by remember { mutableStateOf(false) }
				var savedFile by remember { mutableStateOf(false) }

				Column {
					Button(onClick = {
						showSingleFile = true
					}) {
						Text("Choose File")
					}
					Text("File Chosen: $singleFilePathChosen")

					/////////////////////////////////////////////////////////////////


					Button(onClick = {
						showMultipleFile = true
					}) {
						Text("Choose Multiple Files")
					}
					Text("File Chosen: $multipleFilesPathsChosen")

					/////////////////////////////////////////////////////////////////

					Button(onClick = {
						showDirPicker = true
					}) {
						Text("Choose Directory")
					}
					Text("Directory Chosen: $dirChosen")

					/////////////////////////////////////////////////////////////////

					Button(onClick = {
						showSaveFilePicker = true
					}) {
						Text("Choose Save File")
					}
					Text("Saved File: $savedFile")
				}

				FilePicker(showSingleFile, fileExtensions = listOf("jpg", "png", "plist")) { file ->
					singleFilePathChosen = file?.path ?: "none selected"
					showSingleFile = false
				}

				MultipleFilePicker(
					showMultipleFile,
					fileExtensions = listOf("jpg", "png", "plist")
				) { files ->
					multipleFilesPathsChosen = files?.map { it.path + "\n" } ?: listOf()
					showMultipleFile = false
				}

				DirectoryPicker(showDirPicker) { path ->
					dirChosen = path ?: "none selected"
					showDirPicker = false
				}

				SaveFilePicker(
					show = showSaveFilePicker,
					title = "Some title",
					filename = "newTextFile",
					fileExtension = "txt",
				) { selectedFile ->
					savedFile = selectedFile?.path?.let { path ->
						val result = writeToFile(path, "some nice text for our file")
						result.getOrNull() == true
					} ?: false
					showSaveFilePicker = false
				}
			}
		}
	}
	NSApp?.run()
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun writeToFile(filePath: String, contents: String): Result<Boolean> = runCatching {
	val fileHandle = NSFileHandle.fileHandleForWritingAtPath(filePath)
		?: throw Throwable("couldn't open file handle")
	try {
		val contentsAsNsData = memScoped {
			NSString
				.create(string = contents)
				.dataUsingEncoding(NSUTF8StringEncoding)
		} ?: throw Throwable("contents should only include UTF8 values")
		memScoped {
			val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
				alloc<ObjCObjectVar<NSError?>>().ptr
			val success = fileHandle.writeData(contentsAsNsData, error = errorPointer)
			if (success) true
			else throw Throwable(errorPointer.pointed.value?.localizedDescription)
		}
	} finally {
		fileHandle.closeFile()
	}
}
