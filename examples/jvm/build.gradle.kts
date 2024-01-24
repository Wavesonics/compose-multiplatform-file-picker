plugins {
	kotlin("jvm")
	alias(libs.plugins.jetbrainsCompose)
}

dependencies {
	implementation(project(":mpfilepicker"))
	implementation(compose.desktop.currentOs)
}
