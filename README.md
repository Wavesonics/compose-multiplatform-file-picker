# ComposeMultiplatformFilePicker

A multiplatform compose widget for picking files with each platform's Native File Picker Dialog.

Currently supports: Desktop and Android.

### Include in your project:

```kts
implementation("com.darkrockstudios:mpfilepicker:1.0.0")
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

One known bug right now is that setting the initial directory doesn't seem to effect Android's file picker at all, not sure why that is yet.
