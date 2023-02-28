package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.browser.document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.ItemArrayLike
import org.w3c.dom.asList
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
actual fun FilePicker(
    show: Boolean,
    initialDirectory: String?,
    fileExtensions: List<String>,
    onFileSelected: (String?) -> Unit
) {
    LaunchedEffect(show) {
        if(show) {
            document.selectFilesFromDisk(fileExtensions.first(), true)
        }
        onFileSelected(null) // TODO decide how to pass data
    }
}

@Composable
actual fun DirectoryPicker(
    show: Boolean,
    initialDirectory: String?,
    onFileSelected: (String?) -> Unit
) {
    // TODO
}

//suspend fun selectAndParseFilesFromDisk(): List<HistoryFile> {
//    return document.selectFilesFromDisk(".json").map { readFileAsText(it) }
//}

private suspend fun Document.selectFilesFromDisk(
    accept: String,
    isMultiple: Boolean
): List<File> = suspendCoroutine {
    val tempInput = (createElement("input") as HTMLInputElement).apply {
        type = "file"
        style.display = "none"
        this.accept = accept
        multiple = isMultiple
    }

    tempInput.onchange = { changeEvt ->
        val files = (changeEvt.target.asDynamic().files as ItemArrayLike<File>).asList()
        it.resume(files)
    }

    body!!.append(tempInput)
    tempInput.click()
    tempInput.remove()
}

//private suspend fun readFileAsText(file: File) = suspendCoroutine {
//    val reader = FileReader()
//    reader.onload = { loadEvt ->
//        val content = loadEvt.target.asDynamic().result as String
//        it.resumeWith(Result.success(HistoryFile(file.name, content)))
//    }
//    reader.readAsText(file, "UTF-8")
//}