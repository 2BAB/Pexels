import com.android.build.api.dsl.ManagedVirtualDevice
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    alias(libs.plugins.multiplatform) // 2.
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
}

//applyKtorWasmWorkaround(libs.versions.ktor.get())

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                    freeCompilerArgs.add("-Xjdk-release=${JavaVersion.VERSION_1_8}")
                    // freeCompilerArgs.add("-XXLanguage:+ExplicitBackingFields")
                }
            }
        }
        // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
            dependencies {
                debugImplementation(libs.androidx.testManifest)
                implementation(libs.androidx.junit4)
            }
        }
    }

//    jvm()
//
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//        binaries.executable()
//    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "PexelAI"
            isStatic = true
        }
        it.binaries.all {
            linkerOpts("-L/usr/lib/swift")
            linkerOpts("-rpath", "/usr/lib/swift")
            val aicPathSuffix = when (this.target.konanTarget) {
                KonanTarget.IOS_ARM64 -> "ios-arm64"
                KonanTarget.IOS_X64, KonanTarget.IOS_SIMULATOR_ARM64 -> "ios-arm64_x86_64-simulator"
                else -> null
            }
            aicPathSuffix?.let { p ->
                listOf(
                    "MediaPipeTasksGenAIC",
                    "MediaPipeTasksGenAI"
                ).forEach { f ->
                    linkerOpts("-framework", f, "-F../iosApp/Pods/$f/frameworks/$f.xcframework/$p")
                }
                val swiftPathSuffix = when (this.target.konanTarget) {
                    KonanTarget.IOS_ARM64 -> "iphoneos"
                    KonanTarget.IOS_X64, KonanTarget.IOS_SIMULATOR_ARM64 -> "iphonesimulator"
                    else -> null
                }
                swiftPathSuffix?.let { sp ->
                    val swiftPathPrefix =
                        "/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/lib"
                    linkerOpts("-L$swiftPathPrefix/swift/$sp")
                    linkerOpts("-rpath", "$swiftPathPrefix/swift-5.0/$sp")
                }
            }
        }
    }

    cocoapods {
        name = "PexelAI"

        version = "1.0.1"
        ios.deploymentTarget = "15"

        summary = "Pexel-AI"
        homepage = "https://github.com/JetBrains/kotlin"
//        podfile = project.file("../iosApp/Podfile")

//        pod("FirebaseAuth") {
//            version = "10.16.0"
//        }
        pod("MediaPipeTasksGenAIC") {
            version = "0.10.14"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("MediaPipeTasksGenAI") {
            version = "0.10.14"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

    }

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.transitions)
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
            implementation(libs.napier)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.ktor.client.serialization.kotlinx.json)
            implementation(project(":pexels-api"))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.kstore)
            implementation(libs.kstore.file)
            implementation(libs.mediapipe.genai.android)
        }

//        jvmMain.dependencies {
//            implementation(compose.desktop.currentOs)
//            implementation(libs.kotlinx.coroutines.swing)
//            implementation(libs.ktor.client.okhttp)
//            implementation(libs.kstore)
//            implementation(libs.kstore.file)
//            implementation(libs.appdirs)
//        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.kstore)
            implementation(libs.kstore.file)
        }
    }
}

android {
    namespace = "com.linroid.pexels"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        applicationId = "com.linroid.pexels"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
    }
    // https://developer.android.com/studio/test/gradle-managed-devices
    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices.devices {
            maybeCreate<ManagedVirtualDevice>("pixel5").apply {
                device = "Pixel 5"
                apiLevel = 34
                systemImageSource = "aosp"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
}

//compose.desktop {
//    application {
//        mainClass = "MainKt"
//
//        nativeDistributions {
//            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//            packageName = "Pexels"
//            packageVersion = "1.0.0"
//            buildTypes.release.proguard {
//                configurationFiles.from("rules.pro")
//            }
//        }
//    }
//}

// https://youtrack.jetbrains.com/issue/KTOR-5587
//fun Project.applyKtorWasmWorkaround(version: String) {
//    configurations.all {
//        if (name.startsWith("wasmJs")) {
//            resolutionStrategy.eachDependency {
//                if (requested.group.startsWith("io.ktor") &&
//                    requested.name.startsWith("ktor-client-")) {
//                    useVersion(version)
//                }
//            }
//        }
//    }
//}

buildConfig {
    // BuildConfig configuration here.
    // https://github.com/gmazzo/gradle-buildconfig-plugin#usage-in-kts
    useKotlinOutput { topLevelConstants = true }
    packageName("com.linroid.pexels")
    buildConfigField("PEXELS_API_KEY", "ScVpu45vZekZ0ncguaBKiwyJU0MBMRf4xMzJTkyoZwyEDL0L4LNTcVoK")
}
