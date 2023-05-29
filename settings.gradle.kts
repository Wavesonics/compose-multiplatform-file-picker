pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
//        kotlin("multiplatform").version(extra["kotlin.version"] as String)
//        kotlin("android").version(extra["kotlin.version"] as String)
    }
}

rootProject.name = "MultiplatformFilePicker"

include(":mpfilepicker", ":android", ":desktopExample")
include("webExample")
include("native")
