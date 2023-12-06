package com.darkrockstudios.libraries.mpfilepicker

import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs
import org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_selectFolderDialog

internal fun chooseFile(
	initialDirectory: String,
	fileExtension: String,
	title: String?
): String? = MemoryStack.stackPush().use { stack ->
	val filters = if (fileExtension.isNotEmpty()) fileExtension.split(",") else emptyList()
	val aFilterPatterns = stack.mallocPointer(filters.size)
	filters.forEach {
		aFilterPatterns.put(stack.UTF8("*.$it"))
	}
	aFilterPatterns.flip()
	TinyFileDialogs.tinyfd_openFileDialog(
		title,
		initialDirectory,
		aFilterPatterns,
		null,
		false
	)
}

internal fun chooseFiles(
	initialDirectory: String,
	fileExtension: String,
	title: String?,
): List<String>? = MemoryStack.stackPush().use { stack ->
	val filters = if (fileExtension.isNotEmpty()) fileExtension.split(",") else emptyList()
	val aFilterPatterns = stack.mallocPointer(filters.size)
	filters.forEach {
		aFilterPatterns.put(stack.UTF8("*.$it"))
	}
	aFilterPatterns.flip()
	val t = TinyFileDialogs.tinyfd_openFileDialog(
		/* aTitle = */ title,
		/* aDefaultPathAndFile = */ initialDirectory,
		/* aFilterPatterns = */ aFilterPatterns,
		/* aSingleFilterDescription = */ null,
		/* aAllowMultipleSelects = */ true,
	)
	t?.split("|")
}

internal fun chooseDirectory(
	initialDirectory: String,
	title: String?
): String? = tinyfd_selectFolderDialog(
	title,
	initialDirectory
)
