package com.darkrockstudios.libraries.mpfilepicker.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView

@Composable
actual fun FilePicker(show: Boolean, onFileSelected: (String) -> Unit) {
    LaunchedEffect(show) {
        if(show) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

            val fileChooser = JFileChooser(FileSystemView.getFileSystemView())
            fileChooser.currentDirectory = File(System.getProperty("user.dir"))
            fileChooser.dialogTitle = "Select Directory"
            fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            fileChooser.isAcceptAllFileFilterUsed = true
            fileChooser.selectedFile = null
            val file = if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                fileChooser.selectedFile.toString()
            } else {
                ""
            }
            onFileSelected(file)
        }
    }
}