package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import kotlinx.coroutines.CoroutineScope
import java.io.File

public data class JvmFile(
	override val path: String,
	override val platformFile: File,
) : MPFile<File>

@Composable
public actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	onFileSelected: FileSelected,
) {
	Picker(show) {
		val fileFilter = fileExtensions.joinToString(separator = ",")
		val initialDir = initialDirectory ?: System.getProperty("user.dir")
		val filePath = FileChooser.chooseFile(
			initialDirectory = initialDir,
			fileExtensions = fileFilter
		)
		val mpFile = filePath?.let { JvmFile(filePath, File(filePath)) }
		onFileSelected(mpFile)
	}
}

@Composable
public actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
	onFileSelected: (String?) -> Unit,
) {
	Picker(show) {
		val initialDir = initialDirectory ?: System.getProperty("user.dir")
		val dirPath = FileChooser.chooseDirectory(initialDir)
		onFileSelected(dirPath)
	}
}


/**
 * Hack to make [FilePicker] and [DirectoryPicker] modal.
 */
@Composable
private fun Picker(show: Boolean, content: suspend CoroutineScope.() -> Unit) {
	if (show) {
		Dialog(
			onCloseRequest = {},
			state = DialogState(size = DpSize.Zero),
			undecorated = true,
			resizable = false,
			enabled = false,
			focusable = false,
			content = { LaunchedEffect(Unit, content) },
		)
	}
}