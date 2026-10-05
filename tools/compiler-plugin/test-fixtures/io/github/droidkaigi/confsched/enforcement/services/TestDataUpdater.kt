package io.github.droidkaigi.confsched.enforcement.services

import org.jetbrains.kotlin.codeMetaInfo.CodeMetaInfoRenderer
import org.jetbrains.kotlin.test.model.AfterAnalysisChecker
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.globalMetadataInfoHandler
import org.jetbrains.kotlin.test.services.moduleStructure
import org.jetbrains.kotlin.test.services.sourceFileProvider

internal const val UPDATE_TEST_DATA_PROPERTY = "enforcement.test.updateTestData"

/**
 * Rewrites each testData file with the diagnostics the plugin actually reported, so a new rule's
 * expected markers do not have to be guessed. Enabled by `./gradlew :tools:compiler-plugin:test
 * -Penforcement.updateTestData=true`; review the resulting diff before committing it.
 */
class TestDataUpdater(testServices: TestServices) : AfterAnalysisChecker(testServices) {
    override fun check(thereWereFailures: Boolean) {
        if (System.getProperty(UPDATE_TEST_DATA_PROPERTY) != "true") return

        val handler = testServices.globalMetadataInfoHandler
        val builder = StringBuilder()
        val files = testServices.moduleStructure.modules
            .flatMap { module -> module.files }
            .filterNot { it.isAdditional }
            .sortedBy { it.startLineNumberInOriginalFile }
        for (file in files) {
            val fileBuilder = StringBuilder()
            CodeMetaInfoRenderer.renderTagsToText(
                fileBuilder,
                handler.getReportedMetaInfosForFile(file),
                testServices.sourceFileProvider.getContentOfSourceFile(file),
            )
            // Each file after the first is rendered with its original line offset as leading blank
            // lines; the framework strips them the same way before comparing.
            builder.append(
                fileBuilder.toString()
                    .removePrefix("\n".repeat(file.startLineNumberInOriginalFile.coerceAtLeast(0))),
            )
        }
        testServices.moduleStructure.originalTestDataFiles.single()
            .writeText(builder.toString().trim() + "\n")
    }
}
