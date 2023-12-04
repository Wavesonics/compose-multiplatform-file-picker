package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable

public expect class PlatformFile

public typealias FileSelected = (PlatformFile?) -> Unit

@Composable
public expect fun FilePicker(
	show: Boolean,
	initialDirectory: String? = null,
	fileExtensions: List<String> = emptyList(),
	onFileSelected: FileSelected
)

@Composable
public expect fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String? = null,
	onFileSelected: (String?) -> Unit
)
