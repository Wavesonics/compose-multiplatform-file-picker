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

public actual data class PlatformFile(
	val nsUrl: NSURL,
) {
	public val bytes: ByteArray =
		nsUrl.dataRepresentation.toByteArray()

	@OptIn(ExperimentalForeignApi::class)
	private fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
		usePinned {
			memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
		}
	}
}

@Composable
public actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	title: String?,
	onFileSelected: FileSelected,
) {
	LaunchedEffect(show) {
		if (show) {
			with(NSOpenPanel()) {
				if (initialDirectory != null) directoryURL =
					NSURL.fileURLWithPath(initialDirectory, true)
				allowsMultipleSelection = false
				setAllowedFileTypes(fileExtensions)
				allowsOtherFileTypes = true
				canChooseDirectories = false
				canChooseFiles = true
				if (title != null) message = title

				runModal()

				val fileURL = URL
				val filePath = fileURL?.path
				if (filePath != null)  {
					val platformFile = PlatformFile(fileURL)
					onFileSelected(platformFile)
				} else {
					onFileSelected(null)
				}
			}
		}
	}
}

@Composable
public actual fun MultipleFilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	title: String?,
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
				if (title != null) message = title
				runModal()

				val filesUrls = URLs

				val files: List<PlatformFile> = filesUrls.mapNotNull { file ->
					(file as? NSURL)?.let { nsUrl -> PlatformFile(nsUrl) }
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
	title: String?,
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
				if (title != null) message = title
				runModal()

				val fileURL = URL
				val filePath = fileURL?.path
				onFileSelected(filePath)
			}
		}
	}
}
