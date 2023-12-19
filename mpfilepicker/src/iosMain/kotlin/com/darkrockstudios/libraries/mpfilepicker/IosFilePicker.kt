package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import platform.Foundation.NSFileHandle
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.closeFile
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.fileHandleForWritingAtPath
import platform.posix.memcpy
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
	contents: String,
	onSavedFile: (saved: Result<Boolean>) -> Unit,
) {
	val defaultPath =
		NSSearchPathForDirectoriesInDomains(
			NSDocumentDirectory,
			NSUserDomainMask,
			true
		).first() as String
	val randomFileName = "${filename.split('.').first()}${Random.nextInt()}.${filename.split('.')[1]}"
	val filePath by remember { mutableStateOf("${path?.takeIf { it.isNotBlank() } ?: defaultPath}/$randomFileName") }
	val launcher = remember {
		FilePickerLauncher(
			initialDirectory = path,
			pickerMode = FilePickerLauncher.Mode.Save(filePath),
			onFileSelected = {
				println("onFileSelected")
				onSavedFile(Result.success(it?.firstOrNull() != null))
			},
		)
	}

	LaunchedEffect(show) {
		if (show) {
			val result = createTempFile(path = filePath, contents)
//			val result = writeToFile(filePath, contents)
			if (result.getOrNull() == true) launcher.launchFilePicker()
			else onSavedFile(result)
		}
	}
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun createTempFile(path: String, contents: String): Result<Boolean> = runCatching {
	val contentsAsNsData = memScoped {
		NSString
			.create(string = contents)
			.dataUsingEncoding(NSUTF8StringEncoding)
	} ?: throw Throwable("contents should only include UTF8 values")
	println("creating file at path $path")
	NSFileManager().createFileAtPath(
		path,
		contentsAsNsData,
		null/*emptyMap<Any?, Any>()*/
	)
}


@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun writeToFile(filePath: String, contents: String): Result<Boolean> = runCatching {
	val fileHandle = NSFileHandle.fileHandleForWritingAtPath(filePath)
		?: throw Throwable("couldn't open file handle")
	try {
		val contentsAsNsData = memScoped {
			NSString
				.create(string = contents)
				.dataUsingEncoding(NSUTF8StringEncoding)
		} ?: throw Throwable("contents should only include UTF8 values")
		memScoped {
			val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
				alloc<ObjCObjectVar<NSError?>>().ptr
			val success = fileHandle.writeData(contentsAsNsData, error = errorPointer)
			if (success) true
			else throw Throwable(errorPointer.pointed.value?.localizedDescription)
		}
	} finally {
		fileHandle.closeFile()
	}
}