plugins {
    alias(libs.plugins.droidkaigiPrimitiveKmp)
    alias(libs.plugins.droidkaigiPrimitiveKmpCompose)
    alias(libs.plugins.droidkaigiPrimitiveSpotless)
    alias(libs.plugins.droidkaigiPrimitiveIcons)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:model"))
            api(libs.composeRuntime)
            api(libs.composeMaterial3)
            api(libs.composeUi)
        }
    }
}
