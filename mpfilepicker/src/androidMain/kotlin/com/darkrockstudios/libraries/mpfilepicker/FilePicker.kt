package com.darkrockstudios.libraries.mpfilepicker

import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	onFileSelected: (MPFile?) -> Unit
) {
	val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
		onFileSelected(MPFile.Other(result?.toString()))
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
actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
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