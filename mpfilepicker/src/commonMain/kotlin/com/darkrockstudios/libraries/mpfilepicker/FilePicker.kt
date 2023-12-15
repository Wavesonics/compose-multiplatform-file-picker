package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable

public interface MPFile<out T : Any> {
	// on JS this will be a file name, on other platforms it will be a file path
	public val path: String
	public val platformFile: T
	public suspend fun getFileByteArray(): ByteArray
}

public typealias FileSelected = (MPFile<Any>?) -> Unit

public typealias FilesSelected = (List<MPFile<Any>>?) -> Unit

@Composable
public expect fun FilePicker(
	show: Boolean,
	initialDirectory: String? = null,
	fileExtensions: List<String> = emptyList(),
	title: String? = null,
	onFileSelected: FileSelected,
)

@Composable
public expect fun MultipleFilePicker(
	show: Boolean,
	initialDirectory: String? = null,
	fileExtensions: List<String> = emptyList(),
	title: String? = null,
	onFileSelected: FilesSelected
)

@Composable
public expect fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String? = null,
	title: String? = null,
	onFileSelected: (String?) -> Unit,
)

@Composable
public expect fun SaveFilePicker(
	show: Boolean,
	filename: String,
	path: String?,
	contents: String,
	onComplete: (saved: Boolean) -> Unit,
)