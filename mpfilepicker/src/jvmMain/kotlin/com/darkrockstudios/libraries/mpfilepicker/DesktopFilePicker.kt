package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.io.File

public data class JvmFile(
	override val path: String,
	override val platformFile: File,
) : MPFile<File> {
	override suspend fun getFileByteArray(): ByteArray = platformFile.readBytes()
}

@Composable
public actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	title: String?,
	onFileSelected: FileSelected,
) {
	LaunchedEffect(show) {
		if (show) {
			val fileFilter = if (fileExtensions.isNotEmpty()) {
				fileExtensions.joinToString(",")
			} else {
				""
			}

			val initialDir = initialDirectory ?: System.getProperty("user.dir")
			val filePath = chooseFile(
				initialDirectory = initialDir,
				fileExtension = fileFilter,
				title = title
			)
			if (filePath != null) {
				onFileSelected(JvmFile(filePath, File(filePath)))
			} else {
				onFileSelected(null)
			}

		}
	}
}

@Composable
public actual fun MultipleFilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	onFileSelected: FilesSelected
) {
	LaunchedEffect(show) {
		if (show) {
			val fileFilter = if (fileExtensions.isNotEmpty()) {
				fileExtensions.joinToString(",")
			} else {
				""
			}

			val initialDir = initialDirectory ?: System.getProperty("user.dir")
			val filePaths = chooseFiles(
				initialDirectory = initialDir,
				fileExtension = fileFilter
			)
			if (filePaths != null) {
				onFileSelected(filePaths.map { JvmFile(it, File(it)) })
			} else {
				onFileSelected(null)
			}

		}
	}
}

@Composable
public actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
	title: String?,
	onFileSelected: (String?) -> Unit,
) {
	LaunchedEffect(show) {
		if (show) {
			val initialDir = initialDirectory ?: System.getProperty("user.dir")
			val fileChosen = chooseDirectory(initialDir, title)
			onFileSelected(fileChosen)
		}
	}
}