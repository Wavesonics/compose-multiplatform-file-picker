plugins {
	kotlin("jvm")
	alias(libs.plugins.kotlin.compose)
}

dependencies {
	implementation(project(":mpfilepicker"))
	implementation(compose.desktop.currentOs)
}