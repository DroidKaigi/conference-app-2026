plugins {
    alias(libs.plugins.droidkaigiPrimitiveKmp)
    alias(libs.plugins.droidkaigiPrimitiveKmpCompose)
    alias(libs.plugins.droidkaigiPrimitiveBuildkonfig)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.metro)
    alias(libs.plugins.droidkaigiPrimitiveSpotless)
}

kotlin {
    android {
        namespace = "io.github.droidkaigi.confsched.app.shared"
    }

    sourceSets {
        commonMain.dependencies {
            // api (not implementation): the platform app modules inherit these so Metro aggregates every feature/core contribution into the graph.
            api(project(":core:model"))
            api(project(":core:common"))
            api(project(":core:data"))
            api(project(":core:designsystem"))
            api(project(":core:preview:api"))
            api(project(":core:ui"))
            api(project(":feature:sessions"))
            api(project(":feature:about"))
            api(project(":feature:settings"))
            api(project(":feature:search"))
            api(project(":feature:staff"))
            api(project(":feature:contributors"))
            api(project(":feature:sponsors"))
            api(project(":feature:profilecard"))
            api(project(":feature:favorites"))
            api(project(":feature:eventmap"))
            implementation(libs.composeMaterial3AdaptiveNavigation3)
        }
        androidMain.dependencies {
            // LocalActivity and enableEdgeToEdge, for the status bar icon appearance.
            implementation(libs.androidxActivityCompose)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
