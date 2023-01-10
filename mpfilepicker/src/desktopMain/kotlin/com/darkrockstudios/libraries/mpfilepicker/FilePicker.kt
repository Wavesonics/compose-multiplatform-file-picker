package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun FilePicker(
    show: Boolean,
    initialDirectory: String?,
    fileExtension: String?,
    onFileSelected: (String?) -> Unit
) {
    LaunchedEffect(show) {
        if(show) {
            val initialDir = initialDirectory ?: System.getProperty("user.dir")
            val fileChosen = FileChooser.chooseFile(
                initialDirectory = initialDir,
                fileExtension = fileExtension ?: ""
            )
            onFileSelected(fileChosen)
        }
    }
}

@Composable
actual fun DirectoryPicker(
    show: Boolean,
    initialDirectory: String?,
    onFileSelected: (String?) -> Unit
) {
    LaunchedEffect(show) {
        if(show) {
            val initialDir = initialDirectory ?: System.getProperty("user.dir")
            val fileChosen = FileChooser.chooseDirectory(initialDir)
            onFileSelected(fileChosen)
        }
    }
}