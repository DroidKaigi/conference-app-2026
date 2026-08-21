package droidkaigi.primitive

import droidkaigi.icons.GenerateKaigiIcons
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val generatedDir = layout.buildDirectory.dir("generated/icons")

val generateKaigiIcons = tasks.register<GenerateKaigiIcons>("generateKaigiIcons") {
    iconDirectory.set(layout.projectDirectory.dir("icons"))
    outputDirectory.set(generatedDir)
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    extensions.configure<KotlinMultiplatformExtension> {
        // zip keeps the provider lazy while carrying the task dependency to every consumer of the
        // source set, the way the openapi convention does.
        val generatedSources = generateKaigiIcons.zip(generatedDir) { _, dir -> dir }
        sourceSets.getByName("commonMain").kotlin.srcDir(generatedSources)
    }
}
