package com.darkrockstudios.libraries.mpfilepicker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs
import org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_selectFolderDialog
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter


/**
 * This code is derived from the terrific PACMC project:
 * https://github.com/jakobkmar/pacmc
 */
internal object FileChooser {
	private enum class CallType {
		FILE,
		DIRECTORY
	}

	suspend fun chooseFile(
		initialDirectory: String = System.getProperty("user.dir"),
		fileExtensions: String = ""
	): String? {
		return chooseFile(CallType.FILE, initialDirectory, fileExtensions)
	}

	suspend fun chooseDirectory(
		initialDirectory: String = System.getProperty("user.dir"),
	): String? {
		return chooseFile(CallType.DIRECTORY, initialDirectory)
	}

	private suspend fun chooseFile(
		type: CallType,
		initialDirectory: String,
		fileExtensions: String = ""
	): String? {
		return kotlin.runCatching { chooseFileNative(type, initialDirectory, fileExtensions) }
			.onFailure { nativeException ->
				println("A call to chooseDirectoryNative failed: ${nativeException.message}")

				return kotlin.runCatching { chooseFileSwing(type, initialDirectory, fileExtensions) }
					.onFailure { swingException ->
						println("A call to chooseDirectorySwing failed ${swingException.message}")
					}
					.getOrNull()
			}
			.getOrNull()
	}

	private suspend fun chooseFileNative(
		type: CallType,
		initialDirectory: String,
		fileExtension: String
	): String? {
		return when (type) {
			CallType.FILE -> {
				MemoryStack.stackPush().use { stack ->
					val filters = fileExtension.split(",")
					val aFilterPatterns = stack.mallocPointer(filters.size)
					filters.forEach {
						aFilterPatterns.put(stack.UTF8("*.$it"))
					}
					aFilterPatterns.flip()
					TinyFileDialogs.tinyfd_openFileDialog(
						"Choose File",
						initialDirectory,
						aFilterPatterns,
						null,
						false
					)
				}
			}

			CallType.DIRECTORY -> {
				tinyfd_selectFolderDialog(
					"Choose Directory",
					initialDirectory
				)
			}
		}
	}

	private suspend fun chooseFileSwing(
		type: CallType,
		initialDirectory: String,
		fileExtension: String
	) = withContext(Dispatchers.IO) {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

		val chooser = when (type) {
			CallType.FILE -> {
				JFileChooser(initialDirectory).apply {
					fileSelectionMode = JFileChooser.FILES_ONLY
					acceptAllFileFilter
					isVisible = true
					addChoosableFileFilter(FileNameExtensionFilter(fileExtension, fileExtension))
				}
			}

			CallType.DIRECTORY -> {
				JFileChooser(initialDirectory).apply {
					fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
					isVisible = true
				}
			}
		}

		when (val code = chooser.showOpenDialog(null)) {
			JFileChooser.APPROVE_OPTION -> chooser.selectedFile.absolutePath
			JFileChooser.CANCEL_OPTION -> null
			JFileChooser.ERROR_OPTION -> error("An error occurred while executing JFileChooser::showOpenDialog")
			else -> error("Unknown return code '${code}' from JFileChooser::showOpenDialog")
		}
	}
}
