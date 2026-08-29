plugins {
    alias(libs.plugins.droidkaigiConventionKmpFeature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:model"))
            implementation(project(":core:ui"))
            implementation(libs.qrcodeKotlin)
        }
        commonTest.dependencies {
            implementation(project(":core:model"))
            implementation(kotlin("test"))
            implementation(project(":core:testing"))
        }
    }
}
