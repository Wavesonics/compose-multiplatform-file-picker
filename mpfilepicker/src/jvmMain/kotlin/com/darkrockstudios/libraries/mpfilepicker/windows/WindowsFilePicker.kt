package com.darkrockstudios.libraries.mpfilepicker.windows

import com.darkrockstudios.libraries.mpfilepicker.PlatformFilePicker
import com.darkrockstudios.libraries.mpfilepicker.windows.api.JnaFileChooser

class WindowsFilePicker : PlatformFilePicker {
	private val fileChooser = JnaFileChooser()

	override fun pickFile(
		initialDirectory: String?,
		fileExtensions: List<String>?,
		title: String?
	): String? {
		// Setup file chooser
		fileChooser.apply {
			// Set mode
			mode = JnaFileChooser.Mode.Files

			// Only allow single selection
			isMultiSelectionEnabled = false

			// Set initial directory, title and file extensions
			setup(initialDirectory, fileExtensions, title)
		}

		// Show file chooser
		fileChooser.showOpenDialog(null)

		// Return selected file
		return fileChooser.selectedFile?.absolutePath
	}

	override fun pickFiles(
		initialDirectory: String?,
		fileExtensions: List<String>?,
		title: String?
	): List<String>? {
		// Setup file chooser
		fileChooser.apply {
			// Set mode
			mode = JnaFileChooser.Mode.Files

			// Allow multiple selection
			isMultiSelectionEnabled = true

			// Set initial directory, title and file extensions
			setup(initialDirectory, fileExtensions, title)
		}

		// Show file chooser
		fileChooser.showOpenDialog(null)

		// Return selected files
		return fileChooser.selectedFiles
			.mapNotNull { it?.absolutePath }
			.ifEmpty { null }
	}

	override fun pickDirectory(initialDirectory: String?, title: String?): String? {
		// Setup file chooser
		fileChooser.apply {
			// Set mode
			mode = JnaFileChooser.Mode.Directories

			// Only allow single selection
			isMultiSelectionEnabled = false

			// Set initial directory and title
			setup(initialDirectory, null, title)
		}

		// Show file chooser
		fileChooser.showOpenDialog(null)

		// Return selected directory
		return fileChooser.selectedFile?.absolutePath
	}

	private fun JnaFileChooser.setup(
		initialDirectory: String?,
		fileExtensions: List<String>?,
		title: String?
	) {
		// Set title
		title?.let(::setTitle)

		// Set initial directory
		initialDirectory?.let(::setCurrentDirectory)

		// Set file extension
		if (!fileExtensions.isNullOrEmpty()) {
			val filterName = fileExtensions.joinToString(", ", "Supported Files (", ")")
			addFilter(filterName, *fileExtensions.toTypedArray())
		}
	}
}
