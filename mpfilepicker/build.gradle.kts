@file:Suppress("DSL_SCOPE_VIOLATION") // TODO remove this when Gradle is updated 8.1 https://github.com/gradle/gradle/issues/22797

import java.net.URI

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
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
    explicitApi()
    android {
        publishLibraryVariants("release")
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
//        dependencies {
//            implementation("org.lwjgl.lwjgl-tinyfd:3.1.2")
//        }
    }
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api(compose.uiTooling)
                api(compose.preview)
                api(compose.material)
                api(libs.androidx.appcompat)
                api(libs.androidx.coreKtx)
                api(libs.compose.activity)
                api(libs.kotlinx.coroutines.android)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(libs.junit)
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.uiTooling)
                api(compose.preview)
                api(compose.material)
                api(libs.kotlinx.coroutines.swing)

                val lwjglVersion = "3.3.1"
                listOf("lwjgl", "lwjgl-tinyfd").forEach { lwjglDep ->
                    implementation("org.lwjgl:${lwjglDep}:${lwjglVersion}")
                    listOf(
                        "natives-windows",
                        "natives-windows-x86",
                        "natives-windows-arm64",
                        "natives-macos",
                        "natives-macos-arm64",
                        "natives-linux",
                        "natives-linux-arm64",
                        "natives-linux-arm32"
                    ).forEach { native ->
                        runtimeOnly("org.lwjgl:${lwjglDep}:${lwjglVersion}:${native}")
                    }
                }
            }
        }
        val desktopTest by getting
        val jsMain by getting
    }

    val publicationsFromMainHost = listOf(jvm("desktop"), android()).map { it.name } + "kotlinMultiplatform"

    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    publishing {
        repositories {
            /*
            maven {
                name = "GitHubPackages"
                url = URI("https://maven.pkg.github.com/wavesonics/richtext-compose-multiplatform")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
            */
            maven {
                val releaseRepo = URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotRepo = URI("https://s01.oss.sonatype.org/content/repositories/snapshots/")
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

            // Filter which targets get published
            matching { it.name in publicationsFromMainHost }.all {
                val targetPublication = this@all
                tasks.withType<AbstractPublishToMaven>().matching { it.publication == targetPublication }
                //.configureEach { onlyIf { findProperty("isMainHost") == "true" } }*
            }
        }
    }
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
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}