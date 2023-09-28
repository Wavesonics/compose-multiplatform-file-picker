package com.darkrockstudios.libraries.mpfilepicker

import platform.Foundation.NSURL
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIPresentationController
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeContent
import platform.UniformTypeIdentifiers.UTTypeFolder
import platform.darwin.NSObject

/**
 * Wraps platform specific implementation for launching a File Picker.
 *
 * @param initialDirectory Initial directory that the file picker should open to
 * @param fileExtensions Target file extensions that can be selected. If `null`
 *  only folders are selectable, if empty any file can be selected.
 */
public class FilePickerLauncher(
	private val initialDirectory: String?,
	private val pickerMode: Mode,
	private val onFileSelected: FileSelected,
) {
	public sealed interface Mode {
		public data object Directory : Mode
		public data class File(val extensions: List<String>) : Mode
	}

	private val pickerDelegate = object : NSObject(),
		UIDocumentPickerDelegateProtocol,
		UIAdaptivePresentationControllerDelegateProtocol {

		override fun documentPicker(
			controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>
		) {
			(didPickDocumentsAtURLs.firstOrNull() as? NSURL).let { selected ->
				onFileSelected(selected?.path?.let { IosFile(it, selected) })
			}
		}

		override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
			onFileSelected(null)
		}

		override fun presentationControllerWillDismiss(presentationController: UIPresentationController) {
			(presentationController.presentedViewController as? UIDocumentPickerViewController)
				?.let {
					documentPickerWasCancelled(it)
				}
		}
	}

	private val contentTypes: List<UTType>
		get() = when (pickerMode) {
			is Mode.Directory -> listOf(UTTypeFolder)
			is Mode.File -> pickerMode.extensions
				.mapNotNull { UTType.typeWithFilenameExtension(it) }
				.ifEmpty { listOf(UTTypeContent) }
		}

	private fun createPicker() = UIDocumentPickerViewController(
		forOpeningContentTypes = contentTypes
	).apply {
		delegate = pickerDelegate
		initialDirectory?.let { directoryURL = NSURL(string = it) }
	}

	public fun launchFilePicker() {
		UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
			createPicker(),
			animated = true,
			completion = null
		)
	}
}