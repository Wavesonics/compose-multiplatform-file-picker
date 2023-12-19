package com.darkrockstudios.libraries.mpfilepicker

import com.darkrockstudios.libraries.mpfilepicker.FilePickerLauncher.Mode
import com.darkrockstudios.libraries.mpfilepicker.FilePickerLauncher.Mode.Directory
import com.darkrockstudios.libraries.mpfilepicker.FilePickerLauncher.Mode.File
import com.darkrockstudios.libraries.mpfilepicker.FilePickerLauncher.Mode.MultipleFiles
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIPresentationController
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeContent
import platform.UniformTypeIdentifiers.UTTypeFileURL
import platform.UniformTypeIdentifiers.UTTypeFolder
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.native.concurrent.ThreadLocal
import kotlin.random.Random

/**
 * Wraps platform specific implementation for launching a
 * File Picker.
 *
 * @param initialDirectory Initial directory that the
 *  file picker should open to.
 * @param pickerMode [Mode] to open the picker with.
 *
 */
public class FilePickerLauncher(
	private val initialDirectory: String?,
	private val pickerMode: Mode,
	private val onFileSelected: FilesSelected,
) {

	@ThreadLocal
	public companion object {
		/**
		 * For use only with launching plain (no compose dependencies)
		 * file picker. When a function completes iOS deallocates
		 * unreferenced objects created within it, so we need to
		 * keep a reference of the active launcher.
		 */
		internal var activeLauncher: FilePickerLauncher? = null
	}

	/**
	 * Identifies the kind of file picker to open. Either
	 * [Directory] or [File].
	 */
	public sealed interface Mode {
		/**
		 * Use this mode to open a [FilePickerLauncher] for selecting
		 * folders/directories.
		 */
		public data object Directory : Mode

		/**
		 * Use this mode to open a [FilePickerLauncher] for selecting
		 * multiple files.
		 *
		 * @param extensions List of file extensions that can be
		 *  selected on this file picker.
		 */
		public data class MultipleFiles(val extensions: List<String>) : Mode

		/**
		 * Use this mode to open a [FilePickerLauncher] for selecting
		 * a single file.
		 *
		 * @param extensions List of file extensions that can be
		 *  selected on this file picker.
		 */
		public data class File(val extensions: List<String>) : Mode

		/**
		 * Use this mode to open a [FilePickerLauncher] saving a file.
		 *
		 * @param filepath of the file that is going to be saved
		 */
		public data class Save(val filepath: String) : Mode
	}

	private val pickerDelegate = object : NSObject(),
		UIDocumentPickerDelegateProtocol,
		UIAdaptivePresentationControllerDelegateProtocol {

		override fun documentPicker(
			controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>
		) {
			(didPickDocumentsAtURLs as? List<*>)?.let { list ->
				val files = list.map { file ->
					(file as? NSURL)?.let { nsUrl ->
						nsUrl.path?.let { path ->
							IosFile(path, nsUrl)
						}
					} ?: return@let listOf<IosFile>()
				}

				onFileSelected(files)
			}
		}

		override fun documentPickerWasCancelled(
			controller: UIDocumentPickerViewController
		) {
			onFileSelected(null)
		}

		override fun presentationControllerWillDismiss(
			presentationController: UIPresentationController
		) {
			(presentationController.presentedViewController as? UIDocumentPickerViewController)
				?.let { documentPickerWasCancelled(it) }
		}
	}

	private val contentTypes: List<UTType>
		get() = when (pickerMode) {
			is Directory -> listOf(UTTypeFolder)
			is File -> pickerMode.extensions
				.mapNotNull { UTType.typeWithFilenameExtension(it) }
				.ifEmpty { listOf(UTTypeContent) }

			is MultipleFiles -> pickerMode.extensions
				.mapNotNull { UTType.typeWithFilenameExtension(it) }
				.ifEmpty { listOf(UTTypeContent) }

			is Mode.Save -> listOf(UTTypeFileURL)
		}

	private fun createPicker(): UIDocumentPickerViewController {
		return (if (pickerMode is Mode.Save) {
			val filepathAsNsUrl = NSURL(fileURLWithPath = pickerMode.filepath)
			UIDocumentPickerViewController(
				forExportingURLs = listOf(filepathAsNsUrl)
			)
		} else {
			UIDocumentPickerViewController(
				forOpeningContentTypes = contentTypes,
			)
		}).apply {
			delegate = pickerDelegate
			initialDirectory?.let { directoryURL = NSURL(string = it) }
		}
	}


	public fun launchFilePicker() {
		activeLauncher = this
		val picker = createPicker()
		UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
			// Reusing a closed/dismissed picker causes problems with
			// triggering delegate functions, launch with a new one.
			picker,
			animated = true,
			completion = {
				(picker as? UIDocumentPickerViewController)
					?.allowsMultipleSelection = pickerMode is MultipleFiles
			},
		)
	}
}

public suspend fun launchFilePicker(
	initialDirectory: String? = null,
	fileExtensions: List<String>,
	allowMultiple: Boolean? = false,
): List<MPFile<Any>> = suspendCoroutine { cont ->
	try {
		FilePickerLauncher(
			initialDirectory = initialDirectory,
			pickerMode = if (allowMultiple == true) MultipleFiles(fileExtensions) else File(fileExtensions),
			onFileSelected = { selected ->
				// File selection has ended, no launcher is active anymore
				// dereference it
				FilePickerLauncher.activeLauncher = null
				cont.resume(selected.orEmpty())
			}
		).also { launcher ->
			// We're showing the file picker at this time so we set
			// the activeLauncher here. This might be the last time
			// there's an outside reference to the file picker.
			FilePickerLauncher.activeLauncher = launcher
			launcher.launchFilePicker()
		}
	} catch (e: Throwable) {
		// don't swallow errors
		cont.resumeWithException(e)
	}
}

public suspend fun launchDirectoryPicker(
	initialDirectory: String? = null,
): List<MPFile<Any>> = suspendCoroutine { cont ->
	try {
		FilePickerLauncher(
			initialDirectory = initialDirectory,
			pickerMode = Directory,
			onFileSelected = { selected ->
				// File selection has ended, no launcher is active anymore
				// dereference it
				FilePickerLauncher.activeLauncher = null
				cont.resume(selected.orEmpty())
			},
		).also { launcher ->
			// We're showing the file picker at this time so we set
			// the activeLauncher here. This might be the last time
			// there's an outside reference to the file picker.
			FilePickerLauncher.activeLauncher = launcher
			launcher.launchFilePicker()
		}
	} catch (e: Throwable) {
		cont.resumeWithException(e)
	}
}

public suspend fun launchSaveFilePicker(
	initialDirectory: String? = null,
	filename: String,
	contents: String,
): Boolean = suspendCoroutine { cont ->
	var hasCreatedDir = false
	var randomDirPath = ""
	try {
		val defaultPath =
			NSSearchPathForDirectoriesInDomains(
				NSDocumentDirectory,
				NSUserDomainMask,
				true
			).first() as String

		// We create a random directory so that we respect the filename given to the file
		fun generateRandomDirPath(initialDirectory: String?): String {
			val randomSuffix = Random.nextInt()
			return "${initialDirectory?.takeIf { it.isNotBlank() } ?: defaultPath}/tmp$randomSuffix"
		}

		// We keep trying random values just in case there is a file called tmp7688958943 which
		//  just happens to be the random value we generate. This should be extremely unlikely
		//  and most times this loop will only run once
		// TODO could this fail for an irrecoverable error? If yes, we should check for it
		//  before retrying ad infinitum
		do {
			randomDirPath = generateRandomDirPath(initialDirectory)
			val createDirResult = createTmpDir(randomDirPath)
		} while (createDirResult.getOrNull() != true)
		hasCreatedDir = true
		val filePath = "$randomDirPath/$filename"

		val createFileResult = createTmpFile(path = filePath, contents)
		if (createFileResult.getOrNull() != true) {
			tryDeleteTmpDir(randomDirPath)
			cont.resume(createFileResult.getOrThrow())
		} else {
			FilePickerLauncher(
				initialDirectory = initialDirectory,
				pickerMode = Mode.Save(filePath),
				onFileSelected = { selected ->
					FilePickerLauncher.activeLauncher = null
					tryDeleteTmpDir(randomDirPath)
					cont.resume(selected.orEmpty().isNotEmpty())
				}
			).also { launcher ->
				FilePickerLauncher.activeLauncher = launcher
				launcher.launchFilePicker()
			}
		}
	} catch (e: Throwable) {
		if (hasCreatedDir) tryDeleteTmpDir(randomDirPath)
		cont.resumeWithException(e)
	}
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun createTmpDir(path: String): Result<Boolean> = runCatching {
	memScoped {
		val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
			alloc<ObjCObjectVar<NSError?>>().ptr
		val success = NSFileManager().createDirectoryAtPath(
			path,
			withIntermediateDirectories = false,
			attributes = null,
			errorPointer,
		)
		if (success) true
		else throw Throwable(errorPointer.pointed.value?.localizedDescription)
	}
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun createTmpFile(path: String, contents: String): Result<Boolean> = runCatching {
	val contentsAsNsData = memScoped {
		NSString
			.create(string = contents)
			.dataUsingEncoding(NSUTF8StringEncoding)
	} ?: throw Throwable("contents should only include UTF8 values")
	NSFileManager().createFileAtPath(
		path,
		contentsAsNsData,
		null,
	)
}

private fun tryDeleteTmpDir(path: String) {
	val deleted = deleteTmpDir(path)
	if (deleted.getOrNull() != true) {
		// Log the fact that we couldn't delete the tmp file. We don't pass an error
		//  or false to onSavedFile because this doesn't affect the operation, it's
		//  just some cleanup that failed
		println("couldn't delete the tmp dir")
	}
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun deleteTmpDir(path: String): Result<Boolean> = runCatching {
	memScoped {
		val errorPointer: CPointer<ObjCObjectVar<NSError?>> =
			alloc<ObjCObjectVar<NSError?>>().ptr
		val success = NSFileManager().removeItemAtPath(path, errorPointer)
		if (success) true
		else throw Throwable(errorPointer.pointed.value?.localizedDescription)
	}
}
