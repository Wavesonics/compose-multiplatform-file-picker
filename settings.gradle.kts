enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
	repositories {
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
		google()
		gradlePluginPortal()
		mavenCentral()
	}
}

dependencyResolutionManagement {
	repositories {
		google()
		mavenCentral()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
		mavenLocal()
	}
}

rootProject.name = "MultiplatformFilePicker"

include(":mpfilepicker")
include(":picker-core")
include(":examples:android")
include(":examples:jvm")
include(":examples:web-wasm")
include(":examples:web")
include(":examples:macosX64")
include(":examples:ios")
