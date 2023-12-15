package com.darkrockstudios.libraries.mpfilepicker.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.darkrockstudios.libraries.mpfilepicker.SaveFilePicker

class MainActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			MaterialTheme {
				val fileType = listOf("jpg", "png")

				Column {
					var showSingleFilePicker by remember { mutableStateOf(false) }
					var pathSingleChosen by remember { mutableStateOf("") }

					Button(onClick = {
						showSingleFilePicker = true
					}) {
						Text("Choose File")
					}
					Text("File Chosen: $pathSingleChosen")

					FilePicker(showSingleFilePicker, fileExtensions = fileType) { mpFile ->
						if (mpFile != null) {
							pathSingleChosen = mpFile.path
						}
						showSingleFilePicker = false
					}

					/////////////////////////////////////////////////////////////////

					var showMultipleFilePicker by remember { mutableStateOf(false) }
					var pathMultipleChosen by remember { mutableStateOf(listOf("")) }

					Button(onClick = {
						showMultipleFilePicker = true
					}) {
						Text("Multiple Choose File")
					}
					Text("Multiple File Chosen: $pathMultipleChosen")

					MultipleFilePicker(showMultipleFilePicker, fileExtensions = fileType) { mpFiles ->
						if (mpFiles != null) {
							pathMultipleChosen = mpFiles.map { it.path + "\n" }
						}
						showMultipleFilePicker = false
					}

					/////////////////////////////////////////////////////////////////

					var showDirPicker by remember { mutableStateOf(false) }
					var dirChosen by remember { mutableStateOf("") }

					Button(onClick = {
						showDirPicker = true
					}) {
						Text("Choose Directory")
					}
					Text("Directory Chosen: $dirChosen")

					DirectoryPicker(showDirPicker) { path ->
						dirChosen = path ?: "none selected"
						showDirPicker = false
					}

					/////////////////////////////////////////////////////////////////

					var showSaveFilePicker by remember { mutableStateOf(false) }
					var savedFile by remember { mutableStateOf(false) }

					Button(onClick = {
						showSaveFilePicker = true
					}) {
						Text("Choose Save")
					}
					Text("Saved File: $savedFile")

					SaveFilePicker(
						show = showSaveFilePicker,
						path = null,
						filename = "newFile.txt",
						fileExtension = "plain/text",
						contents = "this is a new test file",
					) {
						showSaveFilePicker = false
						savedFile = true
					}
				}
			}
		}
	}
}