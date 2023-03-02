package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable

@Composable
expect fun FilePicker(
	show: Boolean,
	initialDirectory: String? = null,
	fileExtensions: List<String> = emptyList(),
	onFileSelected: (MPFile?) -> Unit
)

@Composable
expect fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String? = null,
	onFileSelected: (String?) -> Unit
)

sealed interface MPFile {

	// on JS this will be a file name, on other platforms it will be a file path
	val fileNameOrPath: String?

	data class Web(override val fileNameOrPath: String) : MPFile {
		val content: String = "TODO"
	}

	data class Other(override val fileNameOrPath: String?) : MPFile
}