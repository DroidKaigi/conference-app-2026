package droidkaigi.primitive

import droidkaigi.LicensesExportExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.attributes.Attribute
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id("com.mikepenz.aboutlibraries.plugin")
    id("org.jetbrains.compose")
}

val libs = the<LibrariesForLibs>()
val licensesExport = extensions.create<LicensesExportExtension>("licensesExport")

licensesExport.target.convention(
    provider {
        val targets = the<KotlinMultiplatformExtension>().targets
            .filterNot { it.platformType == KotlinPlatformType.common }
        targets.singleOrNull()?.name
            ?: error("$path declares ${targets.size} targets; name the exported one with licensesExport.target")
    },
)
licensesExport.sourceSet.convention(licensesExport.target.map { "${it}Main" })

dependencies {
    // The generated Res class the app reads the export back through belongs to commonMain.
    "commonMainImplementation"(libs.composeComponentsResources)
}

aboutLibraries {
    // Assets outside the dependency graph — the bundled fonts — enter the export as the
    // custom libraries defined here.
    collect.configPath = rootDir.resolve("config/aboutlibraries")
}

afterEvaluate {
    val target = licensesExport.target.get()
    val exportTask = tasks.named("exportLibraryDefinitions${target.replaceFirstChar(Char::uppercaseChar)}")
    val resourcesDir = layout.buildDirectory.dir("generated/aboutLibraries/composeResources")

    // AboutLibraries collects only from configurations named `*CompileClasspath` / `*RuntimeClasspath`.
    // Kotlin/Native names its resolvable one `<target>CompileKlibraries`, so it is mirrored under a
    // name the plugin recognises; the attributes decide which published variant each dependency
    // resolves to and must be copied along with the dependency buckets.
    configurations.findByName("${target}CompileKlibraries")?.let { klibraries ->
        configurations.register("${target}CompileClasspath") {
            isCanBeConsumed = false
            isCanBeResolved = true
            extendsFrom(*klibraries.extendsFrom.toTypedArray())
            klibraries.attributes.keySet().forEach { key ->
                @Suppress("UNCHECKED_CAST")
                attributes.attribute(key as Attribute<Any>, klibraries.attributes.getAttribute(key)!!)
            }
        }
    }

    aboutLibraries {
        // Keyed by variant so a module exporting one target leaves its other targets, whose tasks
        // exist but are never run, on the plugin's default output path.
        exports.create(target) {
            outputFile = resourcesDir.map { it.file("files/aboutlibraries.json") }
        }
    }

    compose.resources {
        packageOfResClass = "io.github.droidkaigi.confsched.${project.name.replace('-', '.')}.generated.resources"
        customDirectory(
            sourceSetName = licensesExport.sourceSet.get(),
            // Mapping through the task provider is what carries the task dependency; the directory
            // itself is a plain build path.
            directoryProvider = exportTask.map { resourcesDir.get() },
        )
    }
}
