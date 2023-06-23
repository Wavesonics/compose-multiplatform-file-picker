package com.darkrockstudios.libraries.mpfilepicker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lwjgl.BufferUtils
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_openFileDialog
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
	): String? = withContext(Dispatchers.IO) {

		when (type) {
			CallType.FILE -> {
				val exts = fileExtension.split(",").map { "*.$it" }
				val extsBuff = createBuffer(exts.toTypedArray())

				try {
					tinyfd_openFileDialog(
						"Choose File",
						initialDirectory,
						extsBuff,
						null,
						false
					)
				} finally {
					MemoryUtil.memFree(extsBuff)
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

private fun createBuffer(list: Array<String>): PointerBuffer {
	val p = PointerBuffer.allocateDirect(list.size)
	p.rewind()
	for (s in list) {
		val bytes = s.toByteArray()
		val buffer: java.nio.ByteBuffer = BufferUtils.createByteBuffer(s.length * Character.BYTES)
		buffer.rewind()
		buffer.put(bytes)
		buffer.flip()
		p.put(MemoryUtil.memAddress(buffer))
	}
	p.flip()
	return p
}
