@file:Suppress("DSL_SCOPE_VIOLATION") // TODO remove this when Gradle is updated 8.1 https://github.com/gradle/gradle/issues/22797

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":mpfilepicker"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MultiplatformFilePicker"
            packageVersion = "1.0.0"
            modules("jdk.unsupported")
        }
    }
}
