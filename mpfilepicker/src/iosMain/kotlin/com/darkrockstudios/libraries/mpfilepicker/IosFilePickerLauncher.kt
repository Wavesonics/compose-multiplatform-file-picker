package com.darkrockstudios.libraries.mpfilepicker

import platform.Foundation.NSURL
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIPresentationController
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeFolder
import platform.darwin.NSObject

public actual class FilePickerLauncher actual constructor(
	private val initialDirectory: String?,
	private val fileExtensions: List<String>,
	private val onFileSelected: FileSelected,
) {

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

	private fun createPicker() = UIDocumentPickerViewController(
		forOpeningContentTypes = fileExtensions
			.takeIf { it.isNotEmpty() }
			?.map { UTType.typeWithFilenameExtension(it) }
			?: listOf(UTTypeFolder)
	).apply {
		delegate = pickerDelegate
		initialDirectory?.let { directoryURL = NSURL(string = it) }
	}

	public actual fun launchFilePicker() {
		UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
			createPicker(),
			animated = true,
			completion = null
		)
	}
}