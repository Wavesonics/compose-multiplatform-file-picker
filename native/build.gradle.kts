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
group = "org.example"
version = "unspecified"

//                         "natives-windows", "natives-windows-x86", "natives-windows-arm64",
//                        "natives-macos", "natives-macos-arm64",
//                        "natives-linux", "natives-linux-arm64", "natives-linux-arm32"