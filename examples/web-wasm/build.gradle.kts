import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.jetbrainsCompose)
}

kotlin {
	@OptIn(ExperimentalWasmDsl::class)
	wasmJs {
		browser {
			commonWebpackConfig {
				outputFileName = "composeApp.js"
			}
		}
		binaries.executable()
	}
	sourceSets {
		val wasmJsMain by getting {
			dependencies {
				implementation(compose.runtime)
				implementation(compose.foundation)
				implementation(compose.material3)
				implementation(project(":mpfilepicker"))
			}
		}
	}
}

compose.experimental {
	web.application {}
}
