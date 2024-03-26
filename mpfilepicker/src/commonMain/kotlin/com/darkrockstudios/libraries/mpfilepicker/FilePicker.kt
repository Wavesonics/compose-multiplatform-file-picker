package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable

expect class PlatformFile {
	suspend fun getBytes(): ByteArray?
}

typealias FileSelected = (PlatformFile?) -> Unit

typealias FilesSelected = (List<PlatformFile>?) -> Unit

@Composable
expect fun FilePicker(
	show: Boolean,
	initialDirectory: String? = null,
	fileExtensions: List<String> = emptyList(),
	title: String? = null,
	onFileSelected: FileSelected,
)

@Composable
expect fun MultipleFilePicker(
	show: Boolean,
	initialDirectory: String? = null,
	fileExtensions: List<String> = emptyList(),
	title: String? = null,
	onFileSelected: FilesSelected
)

@Composable
expect fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String? = null,
	title: String? = null,
	onFileSelected: (String?) -> Unit,
)
