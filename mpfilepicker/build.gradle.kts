import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import java.net.URI

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.jetbrainsCompose)
	id("maven-publish")
	id("signing")
}

val readableName = "Multiplatform File Picker"
val repoUrl = "https://github.com/Wavesonics/compose-multiplatform-file-picker"
group = "com.darkrockstudios"
description = "A multiplatform compose widget for picking files"
version = libs.versions.library.get()

extra.apply {
	set("isReleaseVersion", !(version as String).endsWith("SNAPSHOT"))
}

kotlin {
	androidTarget {
		publishLibraryVariants("release")
	}

	jvm {
		compilations.all {
			kotlinOptions.jvmTarget = "17"
		}
	}

	js(IR) {
		browser()
		binaries.executable()
	}

	@OptIn(ExperimentalWasmDsl::class)
	wasmJs {
		moduleName = "mpfilepicker"
		browser()
		binaries.executable()
	}

	macosX64()

	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64(),
	).forEach {
		it.binaries.framework {
			baseName = "MPFilePicker"
		}
	}

	sourceSets {
		commonMain.dependencies {
			api(compose.runtime)
			api(compose.foundation)
		}

		commonTest.dependencies {
			implementation(kotlin("test"))
		}

		androidMain.dependencies {
			api(compose.uiTooling)
			api(compose.preview)
			api(compose.material)
			api(libs.androidx.appcompat)
			api(libs.androidx.core.ktx)
			api(libs.compose.activity)
			api(libs.kotlinx.coroutines.android)
		}

		jvmMain.dependencies {
			api(compose.uiTooling)
			api(compose.preview)
			api(compose.material)

			implementation(libs.jna)
		}
		val jvmTest by getting
		val jsMain by getting
		val wasmJsMain by getting
	}

	@Suppress("OPT_IN_USAGE")
	compilerOptions {
		freeCompilerArgs = listOf("-Xexpect-actual-classes")
	}

	val javadocJar by tasks.registering(Jar::class) {
		archiveClassifier.set("javadoc")
	}

	publishing {
		repositories {
			maven {
				val releaseRepo =
					URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
				val snapshotRepo =
					URI("https://s01.oss.sonatype.org/content/repositories/snapshots/")
				url = if (extra["isReleaseVersion"] == true) releaseRepo else snapshotRepo
				credentials {
					username = System.getenv("OSSRH_USERNAME") ?: "Unknown user"
					password = System.getenv("OSSRH_PASSWORD") ?: "Unknown password"
				}
			}
		}
		publications {
			publications.withType<MavenPublication> {
				artifact(javadocJar.get())

				pom {
					name.set(readableName)
					description.set(project.description)
					inceptionYear.set("2023")
					url.set(repoUrl)
					developers {
						developer {
							name.set("Adam Brown")
							id.set("Wavesonics")
						}
					}
					licenses {
						license {
							name.set("MIT")
							url.set("https://opensource.org/licenses/MIT")
						}
					}
					scm {
						connection.set("scm:git:git://github.com/Wavesonics/compose-multiplatform-file-picker.git")
						developerConnection.set("scm:git:ssh://git@github.com/Wavesonics/compose-multiplatform-file-picker.git")
						url.set("https://github.com/Wavesonics/compose-multiplatform-file-picker")
					}
				}
			}
		}
	}
}

tasks.withType<AbstractPublishToMaven>().configureEach {
	val signingTasks = tasks.withType<Sign>()
	mustRunAfter(signingTasks)
}

signing {
	val signingKey: String? = System.getenv("SIGNING_KEY")
	val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
	if (signingKey != null && signingPassword != null) {
		useInMemoryPgpKeys(null, signingKey, signingPassword)
		sign(publishing.publications)
	} else {
		println("No signing credentials provided. Skipping Signing.")
	}
}

android {
	namespace = "com.darkrockstudios.libraries.mpfilepicker"
	compileSdk = libs.versions.android.compile.sdk.get().toInt()
	sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
	defaultConfig {
		minSdk = libs.versions.android.min.sdk.get().toInt()
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
}

compose.experimental {
	web.application {}
}
