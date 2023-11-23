package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AppKit.NSOpenPanel
import platform.AppKit.setAllowedFileTypes
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.posix.memcpy

public data class MacOSFile(
	override val path: String,
	override val platformFile: NSURL,
) : MPFile<NSURL> {
	@OptIn(ExperimentalForeignApi::class)
	public fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
		usePinned {
			memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
		}
	}
	override suspend fun getFileByteArray(): ByteArray = platformFile.dataRepresentation.toByteArray()
}

@Composable
public actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	onFileSelected: FileSelected,
) {
	LaunchedEffect(show) {
		if (show) {
			with(NSOpenPanel()) {
				if (initialDirectory != null) directoryURL = NSURL.fileURLWithPath(initialDirectory, true)
				allowsMultipleSelection = false
				setAllowedFileTypes(fileExtensions)
				allowsOtherFileTypes = true
				canChooseDirectories = false
				canChooseFiles = true
				runModal()

				val fileURL = URL
				val filePath = fileURL?.path
				if (filePath != null) onFileSelected(MacOSFile(filePath, fileURL))
				else onFileSelected(null)
			}
		}
	}
}

@Composable
public actual fun MultipleFilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	onFileSelected: FilesSelected,
) {
	LaunchedEffect(show) {
		if (show) {
			with(NSOpenPanel()) {
				if (initialDirectory != null) directoryURL = NSURL.fileURLWithPath(initialDirectory, true)
				allowsMultipleSelection = true
				setAllowedFileTypes(fileExtensions)
				allowsOtherFileTypes = true
				canChooseDirectories = false
				canChooseFiles = true
				runModal()

				val filesUrls = URLs

				val files: List<MacOSFile> = filesUrls.mapNotNull { file ->
					(file as? NSURL)?.let { nsUrl ->
						nsUrl.path?.let { path ->
							MacOSFile(path, nsUrl)
						}
					}
				}

				if (files.isEmpty()) onFileSelected(null)
				else onFileSelected(files)
			}
		}
	}
}

@Composable
public actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
	onFileSelected: (String?) -> Unit
) {
	LaunchedEffect(show) {
		if (show) {
			with(NSOpenPanel()) {
				if (initialDirectory != null) directoryURL = NSURL.fileURLWithPath(initialDirectory, true)
				allowsMultipleSelection = false
				canChooseDirectories = true
				canChooseFiles = false
				canCreateDirectories = true
				runModal()

				val fileURL = URL
				val filePath = fileURL?.path
				onFileSelected(filePath)
			}
		}
	}
}