import droidkaigi.includeDebugFeature
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    // Besides the Compose UI this module hosts, the Compose Gradle plugin syncs the compose
    // resources of the whole dependency graph into the app bundle, and it hangs that off the Xcode
    // entry point of the module declaring the Swift Export binary — this one.
    alias(libs.plugins.droidkaigiPrimitiveKmpCompose)
    alias(libs.plugins.metro)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.droidkaigiPrimitiveLicensesExport)
    alias(libs.plugins.droidkaigiPrimitiveSpotless)
}

// embedSwiftExportForXcode inherits Xcode's CONFIGURATION; a Gradle-driven compilation has none.
val includeDebugFeature = project.includeDebugFeature(
    developmentBuild = providers.environmentVariable("CONFIGURATION").orNull.equals("Debug", ignoreCase = true),
)

kotlin {
    jvmToolchain(21)

    iosArm64()
    iosSimulatorArm64()

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    swiftPMDependencies {
        iosMinimumDeploymentTarget.set("16.0")
        swiftPackage(
            url = "https://github.com/firebase/firebase-ios-sdk",
            version = "12.0.0",
            products = listOf("FirebaseCrashlytics"),
            importedClangModules = listOf("FirebaseCrashlytics", "FirebaseCore"),
        )
    }

    sourceSets {
        iosMain.dependencies {
            implementation(project(":app-shared"))
            implementation(libs.okio)
            implementation(libs.kotlinxSerializationJson)
            if (includeDebugFeature) implementation(project(":feature:debug"))
        }
    }

    @OptIn(ExperimentalSwiftExportDsl::class)
    swiftExport {
        moduleName.set("AppShared")
        flattenPackage.set("io.github.droidkaigi.confsched.app.ios")
    }
}

// Compose resources carry one directory per source set and the two iOS targets share iosMain, so one
// of them has to stand for both: they compile the same source set and therefore resolve the same
// libraries, and the device target is the one that ships. A simulator build shows the same list,
// differing only in the artifact suffix of the coordinates that carry one.
licensesExport {
    target = "iosArm64"
    sourceSet = "iosMain"
}
