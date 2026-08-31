rootProject.name = "conference-app-2026"

include(":tools:compiler-plugin")
include(":tools:ksp-processor")
include(":tools:jetwhale-plugin:protocol")
include(":tools:jetwhale-plugin:host")
include(":core:model")
include(":core:common")
include(":core:data")
include(":core:designsystem")
include(":core:testing")
include(":core:ui")
include(":core:preview:api")
include(":core:preview:impl")
include(":core:preview:wrapper")
include(":feature:sessions")
include(":feature:about")
include(":feature:contributors")
include(":feature:debug")
include(":feature:eventmap")
include(":feature:favorites")
include(":feature:profilecard")
include(":feature:sponsors")
include(":feature:staff")
include(":feature:settings")
include(":feature:search")
include(":feature:doodle")
include(":app-shared")
include(":app-ios-kotlin")
include(":app-desktop")
include(":app-web")
include(":app-android")

pluginManagement {
    includeBuild("gradle-conventions")
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
