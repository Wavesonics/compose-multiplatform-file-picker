package com.darkrockstudios.libraries.mpfilepicker.mac

import com.darkrockstudios.libraries.mpfilepicker.PlatformFilePicker
import com.darkrockstudios.libraries.mpfilepicker.mac.foundation.Foundation
import com.darkrockstudios.libraries.mpfilepicker.mac.foundation.ID

class MacOSFilePicker : PlatformFilePicker {
	override fun pickFile(
		initialDirectory: String?,
		fileExtensions: List<String>?,
		title: String?
	): String? {
		return callNativeMacOSPicker(
			mode = MacOSFilePickerMode.File,
			initialDirectory = initialDirectory,
			fileExtensions = fileExtensions,
			title = title
		)
	}

	override fun pickFiles(
		initialDirectory: String?,
		fileExtensions: List<String>?,
		title: String?
	): List<String>? {
		return callNativeMacOSPicker(
			mode = MacOSFilePickerMode.Files,
			initialDirectory = initialDirectory,
			fileExtensions = fileExtensions,
			title = title
		)
	}

	override fun pickDirectory(initialDirectory: String?, title: String?): String? {
		return callNativeMacOSPicker(
			mode = MacOSFilePickerMode.Directories,
			initialDirectory = initialDirectory,
			fileExtensions = null,
			title = title
		)
	}

	private fun <T> callNativeMacOSPicker(
		mode: MacOSFilePickerMode<T>,
		initialDirectory: String?,
		fileExtensions: List<String>?,
		title: String?,
	): T? {
		val pool = Foundation.NSAutoreleasePool()
		return try {
			var response: T? = null

			Foundation.executeOnMainThread(
				withAutoreleasePool = false,
				waitUntilDone = true,
			) {
				// Create the file picker
				val openPanel = Foundation.invoke("NSOpenPanel", "new")

				// Setup single, multiple selection or directory mode
				mode.setupPickerMode(openPanel)

				// Set the title
				title?.let {
					Foundation.invoke(openPanel, "setMessage:", Foundation.nsString(it))
				}

				// Set initial directory
				initialDirectory?.let {
					Foundation.invoke(openPanel, "setDirectoryURL:", Foundation.nsURL(it))
				}

				// Set file extensions
				fileExtensions?.let { extensions ->
					val items = extensions.map { Foundation.nsString(it) }
					val nsData = Foundation.invokeVarArg("NSArray", "arrayWithObjects:", *items.toTypedArray())
					Foundation.invoke(openPanel, "setAllowedFileTypes:", nsData)
				}

				// Open the file picker
				val result = Foundation.invoke(openPanel, "runModal")

				// Get the path(s) from the file picker if the user validated the selection
				if (result.toInt() == 1) {
					response = mode.getResult(openPanel)
				}
			}

			response
		} finally {
			pool.drain()
		}
	}

	private companion object {
		fun singlePath(openPanel: ID): String? {
			val url = Foundation.invoke(openPanel, "URL")
			val path = Foundation.invoke(url, "path")
			return Foundation.toStringViaUTF8(path)
		}

		fun multiplePaths(openPanel: ID): List<String>? {
			val urls = Foundation.invoke(openPanel, "URLs")
			val urlCount = Foundation.invoke(urls, "count").toInt()

			return (0 until urlCount).mapNotNull { index ->
				val url = Foundation.invoke(urls, "objectAtIndex:", index)
				val path = Foundation.invoke(url, "path")
				Foundation.toStringViaUTF8(path)
			}.ifEmpty { null }
		}
	}

	private sealed class MacOSFilePickerMode<T> {
		abstract fun setupPickerMode(openPanel: ID)
		abstract fun getResult(openPanel: ID): T?

		data object File : MacOSFilePickerMode<String?>() {
			override fun setupPickerMode(openPanel: ID) {
				Foundation.invoke(openPanel, "setCanChooseFiles:", true)
				Foundation.invoke(openPanel, "setCanChooseDirectories:", false)
			}

			override fun getResult(openPanel: ID): String? = singlePath(openPanel)
		}

		data object Files : MacOSFilePickerMode<List<String>>() {
			override fun setupPickerMode(openPanel: ID) {
				Foundation.invoke(openPanel, "setCanChooseFiles:", true)
				Foundation.invoke(openPanel, "setCanChooseDirectories:", false)
				Foundation.invoke(openPanel, "setAllowsMultipleSelection:", true)
			}

			override fun getResult(openPanel: ID): List<String>? = multiplePaths(openPanel)
		}

		data object Directories : MacOSFilePickerMode<String>() {
			override fun setupPickerMode(openPanel: ID) {
				Foundation.invoke(openPanel, "setCanChooseFiles:", false)
				Foundation.invoke(openPanel, "setCanChooseDirectories:", true)
			}

			override fun getResult(openPanel: ID): String? = singlePath(openPanel)
		}
	}
}
