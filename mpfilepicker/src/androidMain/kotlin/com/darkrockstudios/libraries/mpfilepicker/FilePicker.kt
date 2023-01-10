package com.darkrockstudios.libraries.mpfilepicker

import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun FilePicker(
    show: Boolean,
    initialDirectory: String?,
    fileExtension: String?,
    onFileSelected: (String?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
        onFileSelected(result?.toString())
    }

    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
    LaunchedEffect(show) {
        if(show) {
            launcher.launch(arrayOf(mimeType ?: ""))
        }
    }
}

@Composable
actual fun DirectoryPicker(
    show: Boolean,
    initialDirectory: String?,
    onFileSelected: (String?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) { result ->
        onFileSelected(result?.toString())
    }

    LaunchedEffect(show) {
        if(show) {
            launcher.launch(null)
        }
    }
}