package io.github.droidkaigi.confsched.feature.about

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import io.github.droidkaigi.confsched.core.testing.Robot
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AboutScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    private val invocations = mutableMapOf<String, Int>()

    private fun record(name: String): () -> Unit = {
        invocations[name] = (invocations[name] ?: 0) + 1
    }

    fun setupContent(isDebugMenuAvailable: Boolean = true) {
        setScreenContent {
            AboutScreen(
                uiState = AboutScreenUiState(title = "About DroidKaigi", versionName = "1.0.0"),
                onOpenVenueWithMap = record(VENUE),
                onOpenSponsors = record(SPONSORS),
                onOpenContributors = record(CONTRIBUTORS),
                onOpenStaff = record(STAFF),
                onOpenLicenses = record(LICENSES),
                onOpenCodeOfConduct = record(CODE_OF_CONDUCT),
                onOpenPrivacyPolicy = record(PRIVACY),
                onOpenSettings = record(SETTINGS),
                onOpenYoutube = record(YOUTUBE),
                onOpenX = record(X),
                onOpenMedium = record(MEDIUM),
                isDebugMenuAvailable = isDebugMenuAvailable,
                onOpenDebug = record(DEBUG),
            )
        }
    }

    fun clickRow(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).performScrollTo().performClick()
        composeUiTest.waitForIdle()
    }

    fun clickSocial(description: String) {
        composeUiTest.onNodeWithContentDescription(description).performScrollTo().performClick()
        composeUiTest.waitForIdle()
    }

    fun checkRowDisplayed(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).performScrollTo().assertIsDisplayed()
    }

    fun checkRowDoesNotExist(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertDoesNotExist()
    }

    fun checkInvokedOnce(name: String) {
        assertEquals(1, invocations[name] ?: 0, "$name should be invoked exactly once")
    }

    // The test environment picks its own locale, so labels are resolved from the resources the UI
    // draws rather than hard-coded, and the assertions hold whichever locale runs.
    private fun text(label: StringResource): String = runBlocking { getString(label) }

    companion object {
        const val VENUE = "venue"
        const val SPONSORS = "sponsors"
        const val CONTRIBUTORS = "contributors"
        const val STAFF = "staff"
        const val LICENSES = "licenses"
        const val CODE_OF_CONDUCT = "codeOfConduct"
        const val PRIVACY = "privacy"
        const val SETTINGS = "settings"
        const val YOUTUBE = "youtube"
        const val X = "x"
        const val MEDIUM = "medium"
        const val DEBUG = "debug"
    }
}
