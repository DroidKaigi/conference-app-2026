plugins {
    alias(libs.plugins.droidkaigiPrimitiveKmp)
    alias(libs.plugins.droidkaigiPrimitiveKmpCompose)
    alias(libs.plugins.droidkaigiPrimitiveSpotless)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:common"))
            api(project(":core:preview:api"))
            implementation(project(":core:preview:wrapper"))
            api(libs.composeRuntime)
            api(libs.composeComponentsResources)
            api(libs.composeFoundation)
            api(libs.composeMaterial3)
            implementation(libs.composeMaterial3AdaptiveNavigation3)
            api(libs.composeUi)
            api(libs.soilQueryCore)
            api(libs.soilQueryCompose)
            api(libs.soilReacty)
            implementation(libs.coilCompose)
            implementation(libs.coilNetworkKtor3)
            implementation(libs.kotlinxDatetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation(libs.androidxActivityCompose)
            implementation(libs.androidxCore)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinxBrowser)
        }
    }
}
