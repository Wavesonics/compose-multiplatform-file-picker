pluginManagement {
	repositories {
		google()
		gradlePluginPortal()
		mavenCentral()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

rootProject.name = "MultiplatformFilePicker"

include(":mpfilepicker", ":examples:android", ":examples:jvm")
include(":examples:web")
include(":examples:macosX64")
