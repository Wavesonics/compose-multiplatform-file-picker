package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import platform.Foundation.NSURL

public data class IosFile(
	override val path: String,
	override val platformFile: NSURL,
) : MPFile<NSURL>

@Composable
public fun FilePickerIOS(
	show: Boolean,
	initialDirectory: String? = null,
	fileExtensions: List<String> = emptyList(),
	onFileSelected: (IosFile?) -> Unit
) {
	val launcher = remember {
		FilePickerLauncher(
			initialDirectory = initialDirectory,
			pickerMode = FilePickerLauncher.Mode.File(fileExtensions),
			onFileSelected = onFileSelected,
		)
	}

	LaunchedEffect(show) {
		if (show) {
			launcher.launchFilePicker()
		}
	}
}

@Composable
public actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	onFileSelected: FileSelected
): Unit = FilePickerIOS(show, initialDirectory, fileExtensions, onFileSelected)

@Composable
public actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
	onFileSelected: (String?) -> Unit
) {
	val launcher = remember {
		FilePickerLauncher(
			initialDirectory = initialDirectory,
			pickerMode = FilePickerLauncher.Mode.Directory,
			onFileSelected = { onFileSelected(it?.path) },
		)
	}

	LaunchedEffect(show) {
		if (show) {
			launcher.launchFilePicker()
		}
	}
}