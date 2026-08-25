plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.droidkaigiPrimitiveKmpCompose)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.metro)
    alias(libs.plugins.aboutlibrariesAndroid)
    alias(libs.plugins.droidkaigiPrimitiveSpotless)
}

android {
    namespace = "io.github.droidkaigi.confsched"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.droidkaigi.confsched2026"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
        }
        create("prod") {
            dimension = "environment"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

aboutLibraries {
    // Assets outside the dependency graph — the bundled fonts — enter the export as the
    // custom libraries defined here.
    collect.configPath = rootDir.resolve("config/aboutlibraries")
}

dependencies {
    implementation(project(":app-shared"))
    implementation(project(":core:preview:api"))
    "devImplementation"(project(":feature:debug"))
    // Supplies the preview drawables the fake server environment points at; excluded from prod.
    "devImplementation"(project(":core:preview:impl"))
    "prodImplementation"(libs.firebaseCrashlytics)
    implementation(libs.androidxActivityCompose)
    implementation(libs.androidxGlanceAppwidget)
    implementation(libs.androidxGlancePreview)
    debugImplementation(libs.androidxGlanceAppwidgetPreview)
    implementation(libs.androidxDatastorePreferencesCore)
    implementation(libs.okio)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinxCoroutinesTest)
}
