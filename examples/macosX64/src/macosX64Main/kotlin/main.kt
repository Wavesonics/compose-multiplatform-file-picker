
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import platform.AppKit.NSApp
import platform.AppKit.NSApplication

fun main() {
	NSApplication.sharedApplication()
	Window(title = "Youtube history") {
		MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
			Scaffold {
				var show by remember { mutableStateOf(false) }
				var pathChosen by remember { mutableStateOf("") }

				var showDirPicker by remember { mutableStateOf(false) }
				var dirChosen by remember { mutableStateOf("") }

				Column {
					Button(onClick = {
						show = true
					}) {
						Text("Choose File")
					}
					Text("File Chosen: $pathChosen")

					/////////////////////////////////////////////////////////////////

					Button(onClick = {
						showDirPicker = true
					}) {
						Text("Choose Directory")
					}
					Text("Directory Chosen: $dirChosen")
				}

				FilePicker(show, fileExtensions = listOf("jpg", "png", "plist")) { platformFile ->
					pathChosen = platformFile?.nsUrl?.path ?: "none selected"
					show = false
				}

				DirectoryPicker(showDirPicker) { path ->
					dirChosen = path ?: "none selected"
					showDirPicker = false
				}
			}

		}
	}
	NSApp?.run()
}
