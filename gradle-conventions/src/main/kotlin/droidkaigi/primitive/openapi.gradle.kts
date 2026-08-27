package droidkaigi.primitive

import droidkaigi.openapi.trimOpenApiSpec
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.openapi.generator")
}

val generatedDir = layout.buildDirectory.dir("generated/openapi")

openApiGenerate {
    generatorName.set("kotlin")
    // Ktorfit mirrors the Retrofit annotation surface, so the retrofit2 codegen behavior fits;
    // the templates in openapi/templates override its output with Ktorfit equivalents.
    library.set("jvm-retrofit2")
    inputSpec.set(layout.projectDirectory.file("openapi/openapi.yml").asFile.path)
    outputDir.set(generatedDir.map { it.asFile.path })
    templateDir.set(layout.projectDirectory.dir("openapi/templates").asFile.path)
    apiPackage.set("io.github.droidkaigi.confsched.core.data")
    modelPackage.set("io.github.droidkaigi.confsched.core.data")
    additionalProperties.putAll(
        mapOf(
            "serializationLibrary" to "kotlinx_serialization",
            "useCoroutines" to "true",
            "dateLibrary" to "string",
            // Ktorfit KSP only generates for sources whose path contains "commonMain".
            "sourceFolder" to "src/commonMain/kotlin",
        ),
    )
    // Generate only the interfaces and models; Retrofit infrastructure files do not compile in KMP.
    globalProperties.putAll(
        mapOf(
            "apis" to "",
            "models" to "",
            "modelDocs" to "false",
            "apiDocs" to "false",
            "modelTests" to "false",
            "apiTests" to "false",
        ),
    )
    // Spec schema names to app model names: entities take a Response suffix and version
    // suffixes are dropped.
    modelNameMappings.putAll(
        mapOf(
            "TimetableResponseV4" to "TimetableResponse",
            "SessionV4" to "SessionResponse",
            "SessionListResponseV4" to "SessionListResponse",
            "I18nMessage" to "LocaledResponse",
            "SessionRoom" to "RoomResponse",
            "SessionSpeakerV2" to "SpeakerResponse",
            "SpeakerListResponseV2" to "SpeakerListResponse",
            "SessionCategory" to "CategoryResponse",
            "SessionCategoryItem" to "CategoryItemResponse",
            "Asset" to "SessionAssetResponse",
            "Sponsor" to "SponsorResponse",
            "SponsorPlan" to "SponsorPlanResponse",
            "Staff" to "StaffResponse",
            "Contributor" to "ContributorResponse",
            "Prize" to "PrizeResponse",
            "PrizeGroup" to "PrizeGroupResponse",
            "EventProject" to "ProjectResponse",
            "Language" to "LanguageResponse",
            "SessionType" to "SessionTypeResponse",
            "HttpStatus" to "HttpStatusResponse",
        ),
    )
    // Turns "DroidKaigi2026Routes_getTimetable" into "getTimetable" and so on.
    removeOperationIdPrefix.set(true)
    // uuid/uri formats otherwise map to java.util types, which do not exist on non-JVM targets.
    typeMappings.putAll(
        mapOf(
            "UUID" to "kotlin.String",
            "URI" to "kotlin.String",
        ),
    )
}

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    extensions.configure<KotlinMultiplatformExtension> {
        // zip keeps the directory provider lazy while carrying the openApiGenerate dependency to
        // every consumer of the source set (compilations, KSP, IDE import) without explicit
        // dependsOn wiring. The task's own outputDir property cannot be read here: consumers
        // resolve source directories at task-graph time, before the task has run.
        val generatedSources = tasks.named("openApiGenerate")
            .zip(generatedDir) { _, dir -> dir.dir("src/commonMain/kotlin") }
        sourceSets.getByName("commonMain").kotlin.srcDir(generatedSources)
    }
}

// AGP reads a baselineProfiles directory next to every Kotlin source directory, which for the
// generated one lies inside the openApiGenerate output.
tasks.matching { it.name.endsWith("ArtProfile") }.configureEach {
    dependsOn("openApiGenerate")
}

tasks.register("trimOpenApiSpec") {
    description = """
        Trims openapi/openapi.yml in place to one event's paths and the schemas they reach.
        Run after replacing the spec with a fresh copy: ./gradlew ${project.path}:trimOpenApiSpec
    """.trimIndent()

    val specFile = layout.projectDirectory.file("openapi/openapi.yml").asFile
    doLast {
        val result = trimOpenApiSpec(specFile, "droidkaigi2026")
        logger.lifecycle("kept ${result.keptPaths} path(s) and ${result.keptSchemas} schema(s) in $specFile")
    }
}
