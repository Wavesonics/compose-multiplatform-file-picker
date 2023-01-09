package com.darkrockstudios.libraries.mpfilepicker.common

import androidx.compose.runtime.Composable

@Composable
expect fun FilePicker(
    show: Boolean,
    initialDirectory: String? = null,
    fileExtension: String? = null,
    onFileSelected: (String?) -> Unit
)

@Composable
expect fun DirectoryPicker(
    show: Boolean,
    initialDirectory: String? = null,
    onFileSelected: (String?) -> Unit
)