import java.net.URI

val library_version: String by extra

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("maven-publish")
    id("signing")
}

val readableName = "Multiplatform File Picker"
val repoUrl = "https://github.com/Wavesonics/compose-multiplatform-file-picker"
group = "com.darkrockstudios"
description = "A multiplatform compose widget for picking files"
version = library_version

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
                api("androidx.appcompat:appcompat:1.6.0")
                api("androidx.core:core-ktx:1.9.0")
                api("androidx.activity:activity-compose:1.6.1")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.uiTooling)
                api(compose.preview)
                api(compose.material)

                api("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")

                val lwjglVersion = "3.3.1"
                listOf("lwjgl", "lwjgl-tinyfd").forEach { lwjglDep ->
                    implementation("org.lwjgl:${lwjglDep}:${lwjglVersion}")
                    listOf(
                        "natives-windows", "natives-windows-x86", "natives-windows-arm64",
                        "natives-macos", "natives-macos-arm64",
                        "natives-linux", "natives-linux-arm64", "natives-linux-arm32"
                    ).forEach { native ->
                        runtimeOnly("org.lwjgl:${lwjglDep}:${lwjglVersion}:${native}")
                    }
                }
            }
        }
        val desktopTest by getting
        val jsMain by getting
    }

    val publicationsFromMainHost =
        listOf(jvm("desktop"), android()).map { it.name } + "kotlinMultiplatform"

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

            // Filter which targets get published
            matching { it.name in publicationsFromMainHost }.all {
                val targetPublication = this@all
                tasks.withType<AbstractPublishToMaven>()
                    .matching { it.publication == targetPublication }
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