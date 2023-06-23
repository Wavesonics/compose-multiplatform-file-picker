pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "MultiplatformFilePicker"

include(":mpfilepicker", ":examples:android", ":examples:desktop")
include(":examples:web")
