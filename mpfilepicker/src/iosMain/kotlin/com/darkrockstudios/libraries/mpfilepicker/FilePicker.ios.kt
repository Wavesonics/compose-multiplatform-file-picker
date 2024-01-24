package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.posix.memcpy

public actual data class PlatformFile(
	val nsUrl: NSURL,
) {
	public val bytes: ByteArray =
		nsUrl.dataRepresentation.toByteArray()

	@OptIn(ExperimentalForeignApi::class)
	private fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
		usePinned {
			memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
		}
	}
}

@Composable
public actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	title: String?,
	onFileSelected: FileSelected,
) {
	val launcher = remember {
		FilePickerLauncher(
			initialDirectory = initialDirectory,
			pickerMode = FilePickerLauncher.Mode.File(fileExtensions),
			onFileSelected = {
				onFileSelected(it?.firstOrNull())
			},
		)
	}

	LaunchedEffect(show) {
		if (show) {
			launcher.launchFilePicker()
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
	val launcher = remember {
		FilePickerLauncher(
			initialDirectory = initialDirectory,
			pickerMode = FilePickerLauncher.Mode.MultipleFiles(fileExtensions),
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
public actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
	title: String?,
	onFileSelected: (String?) -> Unit,
) {
	val launcher = remember {
		FilePickerLauncher(
			initialDirectory = initialDirectory,
			pickerMode = FilePickerLauncher.Mode.Directory,
			onFileSelected = { onFileSelected(it?.firstOrNull()?.nsUrl?.path) },
		)
	}

	LaunchedEffect(show) {
		if (show) {
			launcher.launchFilePicker()
		}
	}
}
