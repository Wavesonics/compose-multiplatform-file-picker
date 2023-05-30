// TODO remove this when Gradle is updated 8.1 https://github.com/gradle/gradle/issues/22797
@file:Suppress("DSL_SCOPE_VIOLATION")


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    kotlin("android")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":mpfilepicker"))
    implementation(libs.compose.activity)
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "com.darkrockstudios.libraries.mpfilepicker.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}