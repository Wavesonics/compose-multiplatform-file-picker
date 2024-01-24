plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    kotlin("android")
}

dependencies {
    implementation(project(":mpfilepicker"))
    implementation(libs.compose.activity)
}

android {
    compileSdk = libs.versions.android.compile.sdk.get().toInt()
    defaultConfig {
        applicationId = "com.darkrockstudios.libraries.mpfilepicker.android"
        minSdk = libs.versions.android.min.sdk.get().toInt()
        targetSdk = libs.versions.android.target.sdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    namespace = "com.darkrockstudios.libraries.mpfilepicker.android"
}
