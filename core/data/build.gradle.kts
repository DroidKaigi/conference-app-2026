import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.droidkaigiPrimitiveKmp)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.metro)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.droidkaigiPrimitiveOpenapi)
    alias(libs.plugins.ksp)
    alias(libs.plugins.droidkaigiPrimitiveSpotless)
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
        commonMain.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:common"))
            implementation(project(":core:preview:api"))
            api(libs.androidxDatastoreCore)
            api(libs.androidxDatastorePreferencesCore)
            implementation(libs.soilQueryCore)
            implementation(libs.kotlinxCollectionsImmutable)
            implementation(libs.kotlinxCoroutinesCore)
            implementation(libs.kotlinxSerializationJson)
            implementation(libs.ktorClientContentNegotiation)
            implementation(libs.ktorSerializationKotlinxJson)
            implementation(libs.ktorfitLib)
            implementation(libs.aboutlibrariesCore)
        }
        commonTest.dependencies {
            implementation(project(":core:model"))
            implementation(kotlin("test"))
            implementation(libs.kotlinxCollectionsImmutable)
        }
        jvmTest.dependencies {
            implementation(libs.kotlinxCoroutinesTest)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinxBrowser)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":tools:ksp-processor"))
    add("kspCommonMainMetadata", libs.ktorfitKsp)
}

tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
