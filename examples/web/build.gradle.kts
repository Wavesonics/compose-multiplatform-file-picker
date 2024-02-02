plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.jetbrainsCompose)
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
