plugins {
	kotlin("native.cocoapods")
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.jetbrainsCompose)
}

kotlin {
	iosX64()
	iosArm64()
	iosSimulatorArm64()

	sourceSets {
		iosMain.dependencies {
			implementation(compose.ui)
			implementation(compose.foundation)
			implementation(compose.material)
			implementation(compose.runtime)

			implementation(project(":mpfilepicker"))
		}
	}
}


kotlin.cocoapods {
	name = "ios"
	version = libs.versions.library.get()
	summary =
		"A multiplatform compose widget for picking files with each platform''s Native File Picker Dialog."
	homepage = "https://github.com/Wavesonics/compose-multiplatform-file-picker"
	ios.deploymentTarget = "14.1"

	framework {
		baseName = "ios"
	}

	podfile = project.file("../iosApp/Podfile")
}
