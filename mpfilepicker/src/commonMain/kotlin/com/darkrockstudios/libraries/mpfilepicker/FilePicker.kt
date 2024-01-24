package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable

public expect class PlatformFile

public typealias FileSelected = (PlatformFile?) -> Unit

public typealias FilesSelected = (List<PlatformFile>?) -> Unit

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
