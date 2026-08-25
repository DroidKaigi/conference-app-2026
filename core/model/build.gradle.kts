import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.droidkaigiPrimitiveKmp)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.droidkaigiPrimitiveSpotless)
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
        commonMain.dependencies {
            api(libs.kotlinxCollectionsImmutable)
            // DroidKaigi2026Day exposes the date each day falls on.
            api(libs.kotlinxDatetime)
            api(libs.soilQueryCore)
            // The licenses screen reads AboutLibraries' own model, so its key contract exposes it.
            api(libs.aboutlibrariesCore)
            implementation(libs.kotlinxSerializationJson)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":tools:ksp-processor"))
}

tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
