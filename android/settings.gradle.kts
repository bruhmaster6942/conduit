pluginManagement {
    val flutterSdkPath = run {
        val properties = java.util.Properties()
        file("local.properties").inputStream().use { properties.load(it) }
        val flutterSdkPath = properties.getProperty("flutter.sdk")
        require(flutterSdkPath != null) { "flutter.sdk not set in local.properties" }
        flutterSdkPath
    }

    includeBuild("$flutterSdkPath/packages/flutter_tools/gradle")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.flutter.flutter-plugin-loader") version "1.0.0"
    id("com.android.application") version "9.2.1" apply false
    id("org.jetbrains.kotlin.android") version "2.4.0" apply false
}

include(":app")

// Force one shared NDK (29.0, already provisioned locally) for every project that
// uses the Android plugin (app + all flutter plugin subprojects). Without this,
// AGP auto-installs Flutter's pinned 28.2 in a second NDK copy (~2.2G extra disk).
gradle.projectsLoaded {
    rootProject.subprojects {
        // Runs during configuration phase, before each project's evaluation finishes.
        if (name != "app" && name != "flutter") {
            afterEvaluate {
                extensions.findByName("android")?.let { ext ->
                    if (ext is com.android.build.gradle.BaseExtension) {
                        ext.ndkVersion = "29.0.14206865"
                    } else if (ext is com.android.build.gradle.LibraryExtension) {
                        ext.ndkVersion = "29.0.14206865"
                    }
                }
            }
        }
    }
}
