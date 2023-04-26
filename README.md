# Compose Multiplatform File Picker

![MIT License](https://img.shields.io/github/license/Wavesonics/compose-multiplatform-file-picker) ![Latest Release](https://img.shields.io/github/v/release/Wavesonics/compose-multiplatform-file-picker?include_prereleases)

![badge-platform-windows] ![badge-platform-linux] ![badge-platform-macos] ![badge-platform-android] ![badge-platform-js]

![badge-kotlin]

A multiplatform compose widget for picking files with each platform's Native File Picker Dialog.

Currently supports: Desktop and Android.

#### Desktop/JVM Implementation

The native desktop dialog implementation is derived from the [Pacmc project](https://github.com/jakobkmar/pacmc)

See `FileChooser.kt` as well as the `lwjgl` gradle filter.

## Include in your project:

```kts
implementation("com.darkrockstudios:mpfilepicker:1.1.0")
```

## How to use

In your shared jetbrains compose multiplatform code, add one of the following.

To show the dialog, simply set the boolean state to true via a button or what ever you want.

### Pick a file with a filter:

````kotlin
var showFilePicker by remember { mutableStateOf(false) }

val fileType = "jpg"
FilePicker(showFilePicker, fileExtension = fileType) { path ->
    showFilePicker = false
    // do something with path
}
````

### Pick a directory:

````kotlin
var showDirPicker by remember { mutableStateOf(false) }

DirectoryPicker(showDirPicker) { path ->
    showDirPicker = false
    // do something with path
}
````

On each supported platform, it will update the platform native file picker dialog. On desktop it will fall back to the
Swing file picker if the native one can't be use for some reason.

## Windows

![Windows native file picker](screenshot-desktop-windows.jpg "Windows native file picker")

## Android

![Android native file picker](screenshot-android.png "Android native file picker")

## Known Bugs

- Setting the initial directory doesn't seem to effect Android's file picker at all, not sure why that is yet.

[badge-kotlin]: https://img.shields.io/badge/kotlin-1.8.20-blue.svg?logo=kotlin

<!-- PLATFORMS -->

[badge-platform-linux]: http://img.shields.io/badge/platform-linux-2D3F6C.svg?style=flat
[badge-platform-android]: http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat
[badge-platform-ios]: http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat
[badge-platform-windows]: http://img.shields.io/badge/platform-windows-4D76CD.svg?style=flat
[badge-platform-macos]: http://img.shields.io/badge/platform-macos-111111.svg?style=flat
[badge-platform-js]: http://img.shields.io/badge/platform-js-34913c.svg?style=flat
