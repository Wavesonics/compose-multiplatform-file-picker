pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "MultiplatformFilePicker"

include(":mpfilepicker", ":examples:android", ":desktopExample")
include("webExample")
