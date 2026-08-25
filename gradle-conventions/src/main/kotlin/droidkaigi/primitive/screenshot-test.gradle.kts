package droidkaigi.primitive

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.RoborazziExtension
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("io.github.takahirom.roborazzi")
}

val libs = the<LibrariesForLibs>()

// The scan scope always matches the module package (derived from the project path, the same rule
// as the android namespace).
val screenshotPackage = "io.github.droidkaigi.confsched" + project.path.replace(":", ".")

configure<RoborazziExtension> {
    @OptIn(ExperimentalRoborazziApi::class)
    generateComposePreviewDesktopTests {
        enable.set(true)
        packages.set(listOf(screenshotPackage))
        // Previews are private: nothing but the tooling and this scan ever calls one.
        includePrivatePreviews.set(true)
    }
}

kotlin {
    sourceSets.named("commonTest") {
        dependencies {
            implementation(kotlin("test"))
            implementation(project(":core:testing"))
        }
    }

    sourceSets.named("jvmTest") {
        dependencies {
            // Declared directly (not only via :core:testing) — the Roborazzi plugin verifies these
            // exact dependencies on the module when generateComposePreviewDesktopTests is on.
            implementation(libs.roborazziDesktopPreviewScannerSupport)
            implementation(libs.composablePreviewScannerAndroid)
            implementation(libs.junit)
            // Supplies at runtime the impl classes that androidMain sees only at compile time.
            implementation(project(":core:preview:impl"))
        }
    }
}
