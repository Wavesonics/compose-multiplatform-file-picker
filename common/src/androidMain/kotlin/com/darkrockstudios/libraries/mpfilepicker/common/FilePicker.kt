package com.darkrockstudios.libraries.mpfilepicker.common

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun FilePicker(show: Boolean, onFileSelected: (String) -> Unit) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) { result ->
        if(result != null) {
            onFileSelected(result.toString())
        }
    }

    LaunchedEffect(show) {
        if(show) {
            launcher.launch(null)
        }
    }
}