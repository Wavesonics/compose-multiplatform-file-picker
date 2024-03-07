package com.darkrockstudios.libraries.mpfilepicker

import com.darkrockstudios.libraries.mpfilepicker.mac.MacOSFilePicker
import com.darkrockstudios.libraries.mpfilepicker.util.Platform
import com.darkrockstudios.libraries.mpfilepicker.util.PlatformUtil
import com.darkrockstudios.libraries.mpfilepicker.windows.WindowsFilePicker

internal interface PlatformFilePicker {
	fun pickFile(
		initialDirectory: String? = null,
		fileExtensions: List<String>? = null,
		title: String? = null,
	): String?

	fun pickFiles(
		initialDirectory: String? = null,
		fileExtensions: List<String>? = null,
		title: String? = null,
	): List<String>?

	fun pickDirectory(
		initialDirectory: String? = null,
		title: String? = null,
	): String?
}

internal object PlatformFilePickerUtil {
	val current: PlatformFilePicker by lazy { createPlatformFilePicker() }

	private fun createPlatformFilePicker(): PlatformFilePicker {
		return when (PlatformUtil.current) {
			Platform.MacOS -> MacOSFilePicker()
			Platform.Windows -> WindowsFilePicker()
			Platform.Linux -> WindowsFilePicker()		// TODO: WindowsFilePicker is compatible with other platforms but we need to implement native Linux file picker
		}
	}
}
