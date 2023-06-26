plugins {
	kotlin("multiplatform")
}

kotlin {
	macosX64 {
		binaries {
			executable {
				entryPoint = "main"
			}
		}
	}


	sourceSets {
		val macosX64Main by getting
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	google()
}

