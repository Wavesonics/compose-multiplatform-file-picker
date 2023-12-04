allprojects {
	repositories {
		google()
		mavenCentral()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

plugins {
	kotlin("multiplatform") version "1.9.21" apply false
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.android.library) apply false
}