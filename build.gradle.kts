@file:Suppress("DSL_SCOPE_VIOLATION")

// TODO remove this when Gradle is updated 8.1 https://github.com/gradle/gradle/issues/22797

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    kotlin("multiplatform") apply false
    kotlin("android") apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}