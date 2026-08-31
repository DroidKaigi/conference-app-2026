import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.droidkaigiPrimitiveKmpCompose)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.metro)
    alias(libs.plugins.aboutlibrariesAndroid)
    alias(libs.plugins.droidkaigiPrimitiveSpotless)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
}

val keystorePropertiesFile = file("keystore.properties")

android {
    namespace = "io.github.droidkaigi.confsched"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.droidkaigi.confsched2026"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = libs.versions.droidkaigiApp.get()
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            val keystoreProperties = Properties()
            keystorePropertiesFile.inputStream().use(keystoreProperties::load)
            create("prod") {
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
            }
        }
    }

    buildFeatures.resValues = true

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            resValue("string", "app_name", "DroidKaigi 2026 dev")
        }
        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "DroidKaigi 2026")
            signingConfig = signingConfigs.findByName("prod")
            configure<CrashlyticsExtension> {
                // The upload task needs the app id from the Firebase project file.
                mappingFileUploadEnabled = file("src/prod/google-services.json").exists()
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

googleServices {
    // Only the prod source set carries the Firebase project file; dev builds run without it.
    missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN
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
    // Crashlytics transitively pins androidx.fragment 1.1.0, which release lint rejects for
    // the ActivityResult API.
    "prodImplementation"(libs.androidxFragment)
    implementation(libs.androidxActivityCompose)
    implementation(libs.androidxGlanceAppwidget)
    implementation(libs.androidxGlancePreview)
    implementation(libs.androidxWorkRuntime)
    debugImplementation(libs.androidxGlanceAppwidgetPreview)
    implementation(libs.androidxDatastorePreferencesCore)
    implementation(libs.okio)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinxCoroutinesTest)
}
