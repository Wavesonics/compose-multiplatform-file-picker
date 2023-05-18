plugins {
    kotlin("multiplatform")
}

kotlin {
    mingwX64 {
        binaries.executable {
            entryPoint = "main"
            linkerOpts("-mwindows")
        }
    }

    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()

}
