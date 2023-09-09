plugins {
	kotlin("multiplatform")
	alias(libs.plugins.kotlin.compose)
}

kotlin {
	js(IR) {
		browser()
		binaries.executable()
	}
	sourceSets {
		val jsMain by getting {
			dependencies {
				implementation(libs.kotlinx.html)
				implementation(kotlin("stdlib-js"))
				implementation(compose.html.core)
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
