rootProject.name = "Pexels"
// 1.
include(":composeApp")
include(":pexels-api")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
}
