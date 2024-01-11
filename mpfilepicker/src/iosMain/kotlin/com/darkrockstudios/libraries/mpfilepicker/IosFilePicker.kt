package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.posix.memcpy
import kotlin.math.absoluteValue
import kotlin.random.Random

public data class IosFile(
	override val path: String,
	override val platformFile: NSURL,
) : MPFile<NSURL> {
	@OptIn(ExperimentalForeignApi::class)
	public fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
		usePinned {
			memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
		}
	}

	override suspend fun getFileByteArray(): ByteArray =
		platformFile.dataRepresentation.toByteArray()
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
			onFileSelected = { onFileSelected(it?.firstOrNull()?.path) },
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