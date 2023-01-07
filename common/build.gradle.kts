import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

group = "com.darkrockstudios.libraries.mpfilepicker"
version = "1.0-SNAPSHOT"

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.uiTooling)
                api(compose.preview)
                api(compose.foundation)
                api(compose.material)
                //api(compose.material3)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")
                api("androidx.activity:activity-compose:1.6.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                api("org.jetbrains.compose.ui:ui-util:1.2.2")
                api("org.jetbrains.compose.ui:ui-text:1.2.2")
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdkVersion(33)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(33)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}