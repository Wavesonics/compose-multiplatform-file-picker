package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.AppKit.NSOpenPanel
import platform.Foundation.NSURL

public data class MacOSFile(
	override val path: String,
	override val platformFile: NSURL,
) : MPFile<NSURL>

@Composable
public actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	onFileSelected: FileSelected
) {
	val scope = rememberCoroutineScope()
	LaunchedEffect(show) {
		if (show) {
			scope.launch(Dispatchers.Default) {
				val fileFilter = if (fileExtensions.isNotEmpty()) {
					fileExtensions.joinToString(",")
				} else {
					""
				}

				val initialDir = initialDirectory // TODO ?: System.getProperty("user.dir")

				val openPanel = NSOpenPanel()
//				openPanel.directoryURL = NSURL.fileURLWithPath("~/LauncherLogs", isDirectory: true) TODO set initial direcotry
				openPanel.allowsMultipleSelection = false;
				openPanel.canChooseDirectories = false;
				openPanel.canChooseFiles = true;
				openPanel.runModal()

				withContext(Dispatchers.Main) {
//					NSFileHandle()
					val fileURL = openPanel.URL
					val filePath = fileURL?.path
					if (filePath != null)
						onFileSelected(MacOSFile(filePath, fileURL))
				}
			}
		}
	}
}

@Composable
public actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
	onFileSelected: (String?) -> Unit
) {
	val scope = rememberCoroutineScope()
	LaunchedEffect(show) {
		if (show) {
			scope.launch(Dispatchers.Default) {
//                val initialDir = initialDirectory ?: System.getProperty("user.dir")

				withContext(Dispatchers.Main) {
//					onFileSelected(fileChosen)
				}
			}
		}
	}
}