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
}
group = "org.example"
version = "unspecified"