package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.AppKit.NSOpenPanel
import platform.AppKit.setAllowedFileTypes
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
			scope.launch(Dispatchers.Main) {
				val openPanel = NSOpenPanel()
				if (initialDirectory != null)
					openPanel.directoryURL = NSURL.fileURLWithPath(initialDirectory, true)
				openPanel.allowsMultipleSelection = false
				openPanel.setAllowedFileTypes(fileExtensions)
				openPanel.allowsOtherFileTypes = true
				openPanel.canChooseDirectories = false
				openPanel.canChooseFiles = true
				openPanel.runModal()

				val fileURL = openPanel.URL
				val filePath = fileURL?.path
				if (filePath != null)
					onFileSelected(MacOSFile(filePath, fileURL))
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
			scope.launch(Dispatchers.Main) {
				val openPanel = NSOpenPanel()
				if (initialDirectory != null)
					openPanel.directoryURL = NSURL.fileURLWithPath(initialDirectory, true)
				openPanel.allowsMultipleSelection = false
				openPanel.canChooseDirectories = true
				openPanel.canChooseFiles = false
				openPanel.runModal()

				val fileURL = openPanel.URL
				val filePath = fileURL?.path
				if (filePath != null)
					onFileSelected(filePath)
			}
		}
	}
}