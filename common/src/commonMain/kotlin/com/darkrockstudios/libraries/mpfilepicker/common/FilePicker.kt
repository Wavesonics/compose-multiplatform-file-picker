package com.darkrockstudios.libraries.mpfilepicker.common

import androidx.compose.runtime.Composable

@Composable
expect fun FilePicker(show: Boolean, onFileSelected: (String) -> Unit)