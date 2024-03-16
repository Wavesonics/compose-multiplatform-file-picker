package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.posix.memcpy
import kotlin.math.absoluteValue
import kotlin.random.Random

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

@Composable
public actual fun SaveFilePicker(
	show: Boolean,
	title: String?,
	path: String?,
	filename: String,
	fileExtension: String?,
	onFileSelected: FileSelected,
) {
	val defaultPath =
		NSSearchPathForDirectoriesInDomains(
			NSDocumentDirectory,
			NSUserDomainMask,
			true
		).first() as String
	// We create a random directory so that we respect the filename given to the file
	var randomSuffix by remember { mutableIntStateOf(Random.nextInt()) }
	val randomDirPath by remember(randomSuffix) { mutableStateOf("${path?.takeIf { it.isNotBlank() } ?: defaultPath}/tmp$randomSuffix") }
	val filePath by remember(randomDirPath) { mutableStateOf("$randomDirPath/$filename") }
	val launcher = remember {
		FilePickerLauncher(
			initialDirectory = path,
			pickerMode = FilePickerLauncher.Mode.Save(filePath),
			onFileSelected = {
				tryDeleteTmpDir(randomDirPath)
				onFileSelected(it?.firstOrNull())
			},
		)
	}

	LaunchedEffect(show) {
		if (show) {
			// We keep trying random values just in case there is a file called tmp7688958943 which
			//  just happens to be the random value we generate. This should be extremely unlikely
			//  and most times this loop will only run once
			// TODO could this fail with an irrecoverable error? If yes, we should check for it
			//  before retrying ad infinitum
			do {
				randomSuffix = Random.nextInt().absoluteValue
				val createDirResult = createTmpDir(randomDirPath)
			} while (createDirResult.getOrNull() != true)
			val createFileResult = createTmpFile(path = filePath, contents = "")
			if (createFileResult.getOrNull() == true) launcher.launchFilePicker()
			else {
				tryDeleteTmpDir(randomDirPath)
				onFileSelected(null)
			}
		}
	}
}
