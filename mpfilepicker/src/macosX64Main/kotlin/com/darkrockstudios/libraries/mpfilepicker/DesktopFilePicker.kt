package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.AppKit.NSOpenPanel
import platform.AppKit.NSSavePanel
import platform.AppKit.setAllowedFileTypes
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSFileHandle
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.closeFile
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.fileHandleForWritingAtPath
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

	override suspend fun getFileByteArray(): ByteArray =
		platformFile.dataRepresentation.toByteArray()
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
	title: String?,
	onFileSelected: FilesSelected,
) {
	LaunchedEffect(show) {
		if (show) {
			with(NSOpenPanel()) {
				if (initialDirectory != null) directoryURL =
					NSURL.fileURLWithPath(initialDirectory, true)
				allowsMultipleSelection = true
				setAllowedFileTypes(fileExtensions)
				allowsOtherFileTypes = true
				canChooseDirectories = false
				canChooseFiles = true
				if (title != null) message = title
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
	title: String?,
	onFileSelected: (String?) -> Unit
) {
	LaunchedEffect(show) {
		if (show) {
			with(NSOpenPanel()) {
				if (initialDirectory != null) directoryURL =
					NSURL.fileURLWithPath(initialDirectory, true)
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

@Composable
public actual fun SaveFilePicker(
	show: Boolean,
	title: String?,
	path: String?,
	filename: String,
	fileExtension: String?,
	onFileSelected: FileSelected,
) {
	// title, prompt, message, nameFieldLabel
	LaunchedEffect(show) {
		if (show) {
			with(NSSavePanel()) {
				if (path != null) directoryURL = NSURL.fileURLWithPath(path, true)
				canCreateDirectories = true
				nameFieldStringValue = filename
				if (fileExtension != null) message = fileExtension
				runModal()

				val fileURL = URL
				val filePath = fileURL?.path
				if (filePath != null) onFileSelected(MacOSFile(filePath, fileURL))
				else onFileSelected(null)
			}
		}
	}
}
