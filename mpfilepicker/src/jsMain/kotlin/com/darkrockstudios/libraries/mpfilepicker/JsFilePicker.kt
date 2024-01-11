package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.document
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.dom.Document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.ItemArrayLike
import org.w3c.dom.asList
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public data class WebFile(
	override val path: String,
	override val platformFile: File,
) : MPFile<File> {
	public suspend fun getFileContents(): String = readFileAsText(platformFile)
	public override suspend fun getFileByteArray(): ByteArray = readFileAsByteArray(platformFile)
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
			val fixedExtensions = fileExtensions.map { ".$it" }
			val file: List<File> = document.selectFilesFromDisk(
				fixedExtensions.joinToString(","),
				isMultiple = false,
				isDirectory = false,
			)
			onFileSelected(WebFile(file.first().name, file.first()))
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
	LaunchedEffect(show) {
		if (show) {
			val fixedExtensions = fileExtensions.map { ".$it" }
			val file: List<File> = document.selectFilesFromDisk(
				fixedExtensions.joinToString(","),
				isMultiple = true,
				isDirectory = false,
			)
			val webFiles = file.map {
				WebFile(it.name, it)
			}
			onFileSelected(webFiles)
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
	// in a browser we can not pick directories
	throw NotImplementedError("DirectoryPicker is not supported on the web")
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
	// in a browser we can not save files only download of an existing file is supported
	throw NotImplementedError("SaveFilePicker is not supported on the web")
}

private suspend fun Document.selectFilesFromDisk(
	accept: String,
	isMultiple: Boolean,
	isDirectory: Boolean,
): List<File> = suspendCoroutine {
	val tempInput = (createElement("input") as HTMLInputElement).apply {
		type = if (isDirectory) "" else "file"
		style.display = "none"
		this.accept = accept
		multiple = isMultiple
	}

	tempInput.onchange = { changeEvt ->
		val files = (changeEvt.target.asDynamic().files as ItemArrayLike<File>).asList()
		it.resume(files)
	}

	body!!.append(tempInput)
	tempInput.click()
	tempInput.remove()
}

public suspend fun readFileAsText(file: File): String = suspendCoroutine {
	val reader = FileReader()
	reader.onload = { loadEvt ->
		val content = loadEvt.target.asDynamic().result as String
		it.resumeWith(Result.success(content))
	}
	reader.readAsText(file, "UTF-8")
}

public suspend fun readFileAsByteArray(file: File): ByteArray = suspendCoroutine {
	val reader = FileReader()
	reader.onload = {loadEvt ->
		val content = loadEvt.target.asDynamic().result as ArrayBuffer
		val array = Uint8Array(content)
		val fileByteArray = ByteArray(array.length)
			for (i in 0 until array.length) {
				fileByteArray[i] = array[i]
			}
		it.resumeWith(Result.success(fileByteArray))
	}
	reader.readAsArrayBuffer(file)
}