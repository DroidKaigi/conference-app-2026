plugins {
    alias(libs.plugins.droidkaigiPrimitiveKmp)
    alias(libs.plugins.droidkaigiPrimitiveKmpCompose)
    alias(libs.plugins.metro)
    alias(libs.plugins.droidkaigiPrimitiveSpotless)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.composeRuntime)
            api(libs.composeFoundation)
            api(libs.composeMaterial3)
            implementation(libs.composeMaterial3AdaptiveNavigation3)
            api(libs.composeUi)
            api(libs.composeRuntimeRetain)
            api(libs.navigation3Runtime)
            api(libs.navigation3Ui)
            api(libs.kotlinxCoroutinesCore)
            api(libs.kotlinxSerializationJson)
            api(libs.soilQueryCompose)
            api(libs.kermit)
            api(libs.ktorClientCore)
        }
        jvmMain.dependencies {
            implementation(libs.ktorClientCio)
        }
        androidMain.dependencies {
            implementation(libs.ktorClientOkhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktorClientJs)
        }
    }
}
