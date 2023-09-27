package com.darkrockstudios.libraries.mpfilepicker

public expect class FilePickerLauncher(
	initialDirectory: String? = null,
	fileExtensions: List<String> = emptyList(),
	onFileSelected: FileSelected,
) {
	public fun launchFilePicker()
}