package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.io.File

actual data class PlatformFile(
	val file: File,
)

@Composable
actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	title: String?,
	onFileSelected: FileSelected,
) {
	LaunchedEffect(show) {
		if (show) {
			// Get path from native file picker
			val filePicker = PlatformFilePickerUtil.current
			val filePath = filePicker.pickFile(
				initialDirectory = initialDirectory,
				fileExtensions = fileExtensions,
				title = title,
			)

			// Convert path to PlatformFile
			val result = filePath?.let { PlatformFile(File(it)) }

			// Return result
			onFileSelected(result)
		}
	}
}

@Composable
actual fun MultipleFilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	title: String?,
	onFileSelected: FilesSelected
) {
	LaunchedEffect(show) {
		if (show) {
			// Get paths from native file picker
			val filePicker = PlatformFilePickerUtil.current
			val filePaths = filePicker.pickFiles(
				initialDirectory = initialDirectory,
				fileExtensions = fileExtensions,
				title = title,
			)

			// Convert paths to PlatformFile
			val result = filePaths?.map { PlatformFile(File(it)) }

			// Return result
			onFileSelected(result)
		}
	}
}

@Composable
actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
	title: String?,
	onFileSelected: (String?) -> Unit,
) {
	LaunchedEffect(show) {
		if (show) {
			// Get path from native file picker
			val filePicker = PlatformFilePickerUtil.current
			val filePath = filePicker.pickDirectory(
				initialDirectory = initialDirectory,
				title = title,
			)

			// Return result
			onFileSelected(filePath)
		}
	}
}
