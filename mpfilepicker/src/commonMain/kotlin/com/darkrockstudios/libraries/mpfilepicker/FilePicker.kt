package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable

@Composable
expect fun FilePicker(
	show: Boolean,
	initialDirectory: String? = null,
	fileExtensions: List<String> = emptyList(),
	onFileSelected: (String?) -> Unit
)

@Composable
expect fun DirectoryPicker(
    show: Boolean,
    initialDirectory: String? = null,
    onFileSelected: (String?) -> Unit
)