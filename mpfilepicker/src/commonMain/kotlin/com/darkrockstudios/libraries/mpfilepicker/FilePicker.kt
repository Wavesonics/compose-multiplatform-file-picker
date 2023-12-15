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

/**
 *
 *
 * @param onSavedFile saved will be false if no file is selected by the user. If the user selects a
 * file, but we can't write to it then the function is called with a failure
 */
@Composable
public expect fun SaveFilePicker(
	show: Boolean,
	title: String? = null,
	path: String? = null,
	filename: String = "",
	fileExtension: String? = null,
	contents: String,
	onSavedFile: (saved: Result<Boolean>) -> Unit,
)