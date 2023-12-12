# How to Release

1. increase version number in following files
	- examples/ios/ios.podspec
	- gradle/libs.versions.toml
	- examples/iosApp/Podfile.lock
	- README.md - update this file after library is released
2. Create a new Release & Tag on Github, but set it as a pre-release. This is to avoid confusion for
   users when trying to install a library that is uploaded but not yet available for download.
3. Wait for build and upload
4. [SonaType Repo Manager](https://s01.oss.sonatype.org/)
5. Login
6. Go to Staging Repository
7. Close the library
8. Release the library
9. After library is available then update README.md and set the Github Release as the latest release