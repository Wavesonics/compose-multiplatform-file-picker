package com.darkrockstudios.libraries.mpfilepicker

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

public actual data class PlatformFile(
	val uri: Uri,
)

@Composable
public actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	title: String?,
	onFileSelected: FileSelected
) {
	val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
		if (result != null) {
			val platformFile = PlatformFile(result)
			onFileSelected(platformFile)
		} else {
			onFileSelected(null)
		}
	}

	val mimeTypeMap = MimeTypeMap.getSingleton()
	val mimeTypes = if (fileExtensions.isNotEmpty()) {
		fileExtensions.mapNotNull { ext ->
			mimeTypeMap.getMimeTypeFromExtension(ext)
		}.toTypedArray()
	} else {
		emptyArray()
	}

	LaunchedEffect(show) {
		if (show) {
			launcher.launch(mimeTypes)
		}
	}
}

@Composable
public actual fun MultipleFilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	title: String?,
	onFileSelected: FilesSelected
) {
	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.OpenMultipleDocuments()
	) { result ->
		val files = result.map { uri ->
			PlatformFile(uri)
		}

		if (files.isNotEmpty()) {
			onFileSelected(files)
		} else {
			onFileSelected(null)
		}
	}

	val mimeTypeMap = MimeTypeMap.getSingleton()
	val mimeTypes = if (fileExtensions.isNotEmpty()) {
		fileExtensions.mapNotNull { ext ->
			mimeTypeMap.getMimeTypeFromExtension(ext)
		}.toTypedArray()
	} else {
		emptyArray()
	}

	LaunchedEffect(show) {
		if (show) {
			launcher.launch(mimeTypes)
		}
	}
}

@Composable
public actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
	title: String?,
	onFileSelected: (String?) -> Unit
) {
	val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) { result ->
		onFileSelected(result?.toString())
	}

	LaunchedEffect(show) {
		if (show) {
			launcher.launch(null)
		}
	}
}
