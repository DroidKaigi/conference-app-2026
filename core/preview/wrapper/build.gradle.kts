plugins {
    alias(libs.plugins.droidkaigiPrimitiveKmp)
    alias(libs.plugins.droidkaigiPrimitiveKmpCompose)
    alias(libs.plugins.metro)
    alias(libs.plugins.droidkaigiPrimitiveSpotless)
}

kotlin {
    android {
        withHostTest {}
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:preview:api"))
            implementation(project(":core:designsystem"))
            implementation(libs.composeRuntime)
            implementation(libs.composeUi)
            implementation(libs.composeUiToolingPreview)
        }

        // Metro aggregates the resolver binding from impl at this module's compile time, while impl
        // stays off production classpaths. compileOnly is unsupported for Kotlin/Native and
        // Kotlin/Wasm, so the dependency is declared per target instead of in commonMain; those two
        // targets fall back to NoopPreviewImageResolver.
        androidMain.dependencies {
            compileOnly(project(":core:preview:impl"))
        }

        // The screenshot tests render previews on the JVM target.
        jvmMain.dependencies {
            compileOnly(project(":core:preview:impl"))
        }

        val androidHostTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":core:preview:api"))
                // Supplies at runtime the impl classes that androidMain sees only at compile time.
                implementation(project(":core:preview:impl"))
            }
        }
    }
}
