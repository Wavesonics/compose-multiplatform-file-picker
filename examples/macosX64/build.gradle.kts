// TODO remove this when Gradle is updated 8.1 https://github.com/gradle/gradle/issues/22797
@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
	kotlin("multiplatform")
	alias(libs.plugins.kotlin.compose)
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
		val macosX64Main by getting {
			dependencies {
				implementation(compose.ui)
				implementation(compose.foundation)
				implementation(compose.material3)
				implementation(compose.runtime)
				implementation(project(":mpfilepicker"))
			}
		}
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	google()
}

