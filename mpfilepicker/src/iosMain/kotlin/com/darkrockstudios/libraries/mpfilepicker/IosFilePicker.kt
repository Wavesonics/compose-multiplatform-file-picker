package com.darkrockstudios.libraries.mpfilepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeFolder
import platform.darwin.NSObject

public data class IosFile(
	override val path: String,
	override val platformFile: NSURL,
) : MPFile<NSURL>

private class DocumentPickerDelegate(
	private val onFileSelected: (NSURL?) -> Unit,
) : NSObject(),
	UIDocumentPickerDelegateProtocol {
	override fun documentPicker(
		controller: UIDocumentPickerViewController,
		didPickDocumentsAtURLs: List<*>
	) {
		onFileSelected(didPickDocumentsAtURLs.firstOrNull() as? NSURL)
	}

	override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
		onFileSelected(null)
	}
}

@Composable
private fun rememberDocumentPickerViewController(
	onFileSelected: (NSURL?) -> Unit,
	fileTypes: List<*>,
	initialDirectory: String?
): UIDocumentPickerViewController {
	// Remember the delegate, it gets deallocated if not
	// stored outside the function, see: https://stackoverflow.com/a/59976378/2673831
	val delegate = remember { DocumentPickerDelegate(onFileSelected) }

	return remember {
		val docPickerController = UIDocumentPickerViewController(forOpeningContentTypes = fileTypes)
		docPickerController.delegate = delegate
		initialDirectory?.let { docPickerController.directoryURL = NSURL(string = it) }

		docPickerController
	}
}

@Composable
public actual fun FilePicker(
	show: Boolean,
	initialDirectory: String?,
	fileExtensions: List<String>,
	onFileSelected: FileSelected
) {
	val picker = rememberDocumentPickerViewController(
		onFileSelected = { selected ->
			onFileSelected(selected?.path?.let { IosFile(it, selected) })
		},
		fileTypes = fileExtensions.map { UTType.typeWithFilenameExtension(it) },
		initialDirectory = initialDirectory
	)

	LaunchedEffect(show) {
		if (show) {
			UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
				picker,
				animated = true,
				completion = null
			)
		}
	}
}

@Composable
public actual fun DirectoryPicker(
	show: Boolean,
	initialDirectory: String?,
	onFileSelected: (String?) -> Unit
) {
	val picker = rememberDocumentPickerViewController(
		onFileSelected = { onFileSelected(it?.path) },
		fileTypes = listOf(UTTypeFolder),
		initialDirectory = initialDirectory
	)

	LaunchedEffect(show) {
		if (show) {
			UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
				picker,
				animated = true,
				completion = null
			)
		}
	}
}