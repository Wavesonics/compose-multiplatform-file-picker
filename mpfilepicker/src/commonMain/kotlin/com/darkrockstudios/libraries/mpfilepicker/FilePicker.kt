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
	onFileSelected: FileSelected
)

@Composable
public expect fun MultipleFilePicker(
	show: Boolean,
	initialDirectory: String? = null,
	fileExtensions: List<String> = emptyList(),
	onFileSelected: FilesSelected
)

@Composable
public expect fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String? = null,
	onFileSelected: (String?) -> Unit
)