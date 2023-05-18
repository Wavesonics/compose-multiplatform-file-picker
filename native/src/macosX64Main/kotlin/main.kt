import platform.AppKit.NSOpenPanel

fun main() {
    val openPanel = NSOpenPanel()
    openPanel.allowsMultipleSelection = false;
    openPanel.canChooseDirectories = false;
    openPanel.canCreateDirectories = false;
    openPanel.canChooseFiles = true;
    openPanel.runModal()

    println(openPanel.URL)
}