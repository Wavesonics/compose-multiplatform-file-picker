import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import com.darkrockstudios.libraries.mpfilepicker.*
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	CanvasBasedWindow(canvasElementId = "ComposeTarget", title = "") { ExampleView() }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ExampleView() {
	val scope = rememberCoroutineScope()

	var pickerVisible by remember { mutableStateOf(false) }
	var multipickerVisible by remember { mutableStateOf(false) }

	var fileSelected by remember { mutableStateOf(false) }
	val fileNames = remember { mutableStateListOf<String>() }
	val fileContents = remember { mutableStateListOf<String>() }
	val fileContentsRaw = remember { mutableStateListOf<String>() }

	suspend fun addFile(
		file: PlatformFile,
	) {
		val content = readFileAsByteArray(file.file)

		fileNames.add(file.file.name)
		fileContents.add(content.decodeToString())
		fileContentsRaw.add(Base64.encode(content))
	}

	FilePicker(
		pickerVisible,
	) {
		pickerVisible = false
		fileNames.clear()
		fileContents.clear()

		if (it != null) {
			fileSelected = true

			scope.launch {
				addFile(it)
			}
		} else {
			fileSelected = false
		}
	}

	MultipleFilePicker(
		multipickerVisible,
	) {
		multipickerVisible = false
		fileNames.clear()
		fileContents.clear()

		if (it?.isNotEmpty() == true) {
			fileSelected = true

			scope.launch {
				it.forEach { file ->
					addFile(file)
				}
			}
		} else {
			fileSelected = false
		}
	}

	MaterialTheme {
		Column(modifier = Modifier.fillMaxSize()) {
			Row(
				modifier = Modifier.padding(10.dp),
				horizontalArrangement = Arrangement.spacedBy(10.dp)
			) {
				// Button to open the file picker
				Button(onClick = {
					pickerVisible = true
					multipickerVisible = false
				}) {
					Text("Open File Picker")
				}

				// Button to open the multiple file picker
				Button(onClick = {
					pickerVisible = false
					multipickerVisible = true
				}) {
					Text("Open Multi-File Picker")
				}
			}

			AnimatedVisibility(fileSelected) {
				Text("Selected Files:")
			}
			AnimatedVisibility(!fileSelected) {
				Text("No files selected")
			}

			LazyColumn(contentPadding = PaddingValues(10.dp)) {
				fileNames.forEachIndexed { idx, fileName ->
					item {
						Card {
							Column {
								Text("File: $fileName")
								if (fileContents.getOrNull(idx)?.contains('\uFFFD') == false) {
									Text("Content:")
									FileContent(fileContents.getOrNull(idx))
								}

								Text("Raw Content (Base64):")
								FileContent(fileContentsRaw.getOrNull(idx))
							}
						}
						Spacer(modifier = Modifier.height(10.dp))
					}
				}
			}
		}
	}
}

@Composable
private fun FileContent(fileContent: String?) {
	Text(
		fileContent ?: "N/A",
		modifier = Modifier
			.background(
				Color.LightGray.copy(alpha = 0.5f),
				MaterialTheme.shapes.medium
			)
			.width(1200.dp)
			.padding(5.dp),
		fontFamily = FontFamily.Monospace,
		color = Color.Black,
		overflow = TextOverflow.Ellipsis,
		softWrap = true
	)
}

