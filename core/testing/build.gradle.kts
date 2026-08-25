plugins {
    alias(libs.plugins.droidkaigiPrimitiveKmp)
    alias(libs.plugins.droidkaigiPrimitiveKmpCompose)
    alias(libs.plugins.droidkaigiPrimitiveSpotless)
    alias(libs.plugins.metro)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:common"))
            implementation(project(":core:data"))
            api(project(":core:model"))
            api(libs.molecule)
            api(libs.turbine)
            api(libs.kotlinxCoroutinesTest)
        }
        commonMain.dependencies {
            api(libs.composeUiTest)
            implementation(project(":core:preview:api"))
            implementation(project(":core:preview:impl"))
            implementation(project(":core:preview:wrapper"))
        }
        jvmMain.dependencies {
            api(compose.desktop.currentOs)
            api(libs.roborazziComposeDesktop)
        }
    }
}
