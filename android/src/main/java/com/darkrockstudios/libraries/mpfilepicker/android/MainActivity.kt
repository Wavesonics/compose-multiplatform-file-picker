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
import com.darkrockstudios.libraries.mpfilepicker.common.FilePicker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Column {
                    var show by remember { mutableStateOf(false) }
                    var pathChosen by remember { mutableStateOf("") }

                    Button(onClick = {
                        show = true
                    }) {
                        Text("Choose File")
                    }
                    Text("File Chosen: $pathChosen")

                    FilePicker(show) { path ->
                        pathChosen = path
                        show = false
                    }
                }
            }
        }
    }
}