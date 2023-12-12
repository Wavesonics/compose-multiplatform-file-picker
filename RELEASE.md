# How to Release

1. increase version number in following files
	- examples/ios/ios.podspec
	- gradle/libs.versions.toml
	- examples/iosApp/Podfile.lock
	- README.md
2. Create a new Release & Tag on Github
3. Wait for build and upload
4. [SonaType Repo Manager](https://s01.oss.sonatype.org/)
5. Login
6. Go to Staging Repository
7. Close the library
8. Release the library