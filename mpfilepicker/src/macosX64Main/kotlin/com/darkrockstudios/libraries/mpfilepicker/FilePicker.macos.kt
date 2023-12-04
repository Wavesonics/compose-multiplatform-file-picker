package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.AppKit.NSOpenPanel
import platform.AppKit.setAllowedFileTypes
import platform.Foundation.NSURL

public actual data class PlatformFile(
	val nsUrl: NSURL
)

@Composable
public actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	onFileSelected: FileSelected
) {
	LaunchedEffect(show) {
		if (show) {
			with(NSOpenPanel()) {
				if (initialDirectory != null) directoryURL =
					NSURL.fileURLWithPath(initialDirectory, true)
				allowsMultipleSelection = false
				setAllowedFileTypes(fileExtensions)
				allowsOtherFileTypes = true
				canChooseDirectories = false
				canChooseFiles = true
				runModal()

				val fileURL = URL
				val filePath = fileURL?.path
				if (filePath != null)  {
					val platformFile = PlatformFile(fileURL)
					onFileSelected(platformFile)
				} else {
					onFileSelected(null)
				}
			}
		}
	}
}

@Composable
public actual fun DirectoryPicker(
	show: Boolean, initialDirectory: String?, onFileSelected: (String?) -> Unit
) {
	LaunchedEffect(show) {
		if (show) {
			with(NSOpenPanel()) {
				if (initialDirectory != null) directoryURL =
					NSURL.fileURLWithPath(initialDirectory, true)
				allowsMultipleSelection = false
				canChooseDirectories = true
				canChooseFiles = false
				canCreateDirectories = true
				runModal()

				val fileURL = URL
				val filePath = fileURL?.path
				onFileSelected(filePath)
			}
		}
	}
}