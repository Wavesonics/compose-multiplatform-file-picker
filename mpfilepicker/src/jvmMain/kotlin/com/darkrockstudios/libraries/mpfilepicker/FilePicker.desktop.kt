package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.darkrockstudios.libraries.mpfilepicker.windows.api.JnaFileChooser
import java.io.File


public actual data class PlatformFile(
	val file: File,
)

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
			val fileFilter = if (fileExtensions.isNotEmpty()) {
				fileExtensions.joinToString(",")
			} else {
				""
			}

			val chooser = JnaFileChooser()
			chooser.mode = JnaFileChooser.Mode.Directories
			chooser.showOpenDialog(null)
			chooser.selectedFile?.let {
				onFileSelected(PlatformFile(it))
			} ?: onFileSelected(null)

//			if (chooser.showOpenDialog(parent = null)) {
//				chooser.selectedFile?.let {
//					onFileSelected(PlatformFile(it))
//				}
//			} else {
//				onFileSelected(null)
//			}


//			val pool = Foundation.NSAutoreleasePool()
//			try {
//				Foundation.executeOnMainThread(
//					withAutoreleasePool = false,
//					waitUntilDone = true,
//				) {
//					val openPanel = Foundation.invoke("NSOpenPanel", "new")
//					Foundation.invoke(openPanel, "runModal")
//					val url = Foundation.invoke(openPanel, "URL")
//					val path = Foundation.invoke(url, "path")
//					val filePath = Foundation.toStringViaUTF8(path)
//					if (filePath != null) {
//						val file = File(filePath)
//						onFileSelected(PlatformFile(file))
//					} else {
//						onFileSelected(null)
//					}
//				}
//			} finally {
//				pool.drain()
//			}

//			val foundationLibrary = Native.load("Foundation", FoundationLibrary::class.java)
//			val handler = Proxy.getInvocationHandler(foundationLibrary) as Library.Handler
//			val nativeLibrary = handler.nativeLibrary
//			val objcMsgSend = nativeLibrary.getFunction("objc_msgSend")
//
//			val className = "NSOpenPanel"
//			val objecClass = foundationLibrary.objc_getClass(className)
//
//			val selectorStr = "alloc"
//			val selector = foundationLibrary.sel_registerName(selectorStr)
//
//			val args = arrayOf(
//				objecClass,
//				selector,
//			)
//
//			val res = objcMsgSend.invokeLong(args)
//			val resPointer = Pointer(res)
//			val nsOpenPanel = NSOpenPanel::class.java.cast(Proxy.newProxyInstance(
//				NSOpenPanel::class.java.classLoader,
//				arrayOf(NSOpenPanel::class.java),
//				))
//
//			print("res: $nsOpenPanel")


//			val initialDir = initialDirectory ?: System.getProperty("user.dir")
//			val filePath = chooseFile(
//				initialDirectory = initialDir,
//				fileExtension = fileFilter,
//				title = title
//			)
//			if (filePath != null) {
//				val file = File(filePath)
//				val platformFile = PlatformFile(file)
//				onFileSelected(platformFile)
//			} else {
//				onFileSelected(null)
//			}

		}
	}
}

@Composable
public actual fun MultipleFilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	title: String?,
	onFileSelected: FilesSelected
) {
	LaunchedEffect(show) {
		if (show) {
			val fileFilter = if (fileExtensions.isNotEmpty()) {
				fileExtensions.joinToString(",")
			} else {
				""
			}

			val initialDir = initialDirectory ?: System.getProperty("user.dir")
			val filePaths = chooseFiles(
				initialDirectory = initialDir,
				fileExtension = fileFilter,
				title = title
			)
			if (filePaths != null) {
				onFileSelected(filePaths.map { PlatformFile(File(it)) })
			} else {
				onFileSelected(null)
			}

		}
	}
}

@Composable
public actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
	title: String?,
	onFileSelected: (String?) -> Unit,
) {
	LaunchedEffect(show) {
		if (show) {
			val initialDir = initialDirectory ?: System.getProperty("user.dir")
			val fileChosen = chooseDirectory(initialDir, title)
			onFileSelected(fileChosen)
		}
	}
}

//interface NSOpenPanel : Library {
//	companion object {
//		val INSTANCE: NSOpenPanel = Native.load("NSOpenPanel", NSOpenPanel::class.java)
//	}
//}
//
//interface FoundationLibrary : Library {
//	fun objc_getClass(className: String?): Pointer
//
//	fun sel_registerName(selectorName: String): Pointer
//}
//
//class ObjcToJava : InvocationHandler {
//	override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any {
//		TODO("Not yet implemented")
//	}
//}
