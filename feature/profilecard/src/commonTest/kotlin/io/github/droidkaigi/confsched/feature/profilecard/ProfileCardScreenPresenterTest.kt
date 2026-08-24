package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.testing.PresenterTestScope
import io.github.droidkaigi.confsched.core.testing.compositionLocalProviderWithReturnValue
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import io.github.vinceglb.filekit.PlatformFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProfileCardScreenPresenterTest {

    private val graph = createGraph<ProfileCardScreenTestGraph>()
    private val sampleAvatarImage = PlatformFile("avatar.png")

    // stringResource() reads LocalDensity even for plain strings; the presenter test harness has
    // no rendering surface to provide one, so this stands in for it.
    private val presenter: @Composable context(ProfileCardPresenterContext) (ScreenChannel<ProfileCardScreenAction, Nothing>) -> ProfileCardScreenUiState =
        { channel ->
            compositionLocalProviderWithReturnValue(LocalDensity provides Density(density = 1f)) {
                profileCardScreenPresenter(screenChannel = channel)
            }
        }

    @Test
    fun submit_with_every_field_empty_reports_all_four_required_errors_and_stays_on_the_form() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = presenter,
        ) {
            uiStates.awaitItem()

            send(ProfileCardScreenAction.Submit)
            val afterSubmit = assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem())
            assertNotNull(afterSubmit.nickNameErrorMessage)
            assertNotNull(afterSubmit.occupationErrorMessage)
            assertNotNull(afterSubmit.linkErrorMessage)
            assertNotNull(afterSubmit.avatarImageErrorMessage)
        }
    }

    @Test
    fun submit_with_a_malformed_link_reports_a_different_error_than_an_empty_one() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = presenter,
        ) {
            uiStates.awaitItem()

            send(ProfileCardScreenAction.Submit)
            val emptyLinkErrorMessage = assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem()).linkErrorMessage
            assertNotNull(emptyLinkErrorMessage)

            send(ProfileCardScreenAction.UpdateLink("not a url"))
            uiStates.awaitItem()

            send(ProfileCardScreenAction.Submit)
            val malformedLinkErrorMessage = assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem()).linkErrorMessage
            assertNotNull(malformedLinkErrorMessage)
            assertNotEquals(emptyLinkErrorMessage, malformedLinkErrorMessage)
        }
    }

    @Test
    fun submit_with_every_field_filled_in_transitions_to_the_card() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = presenter,
        ) {
            uiStates.awaitItem()

            send(ProfileCardScreenAction.UpdateNickName("droidkaigi"))
            uiStates.awaitItem()
            send(ProfileCardScreenAction.UpdateOccupation("Software Engineer"))
            uiStates.awaitItem()
            send(ProfileCardScreenAction.UpdateLink("https://example.com/user"))
            uiStates.awaitItem()
            send(ProfileCardScreenAction.UpdateAvatarImage(sampleAvatarImage))
            uiStates.awaitItem()

            send(ProfileCardScreenAction.Submit)
            val afterSubmit = assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem())
            assertEquals("droidkaigi", afterSubmit.nickName)
            assertEquals("Software Engineer", afterSubmit.occupation)
            assertEquals("https://example.com/user", afterSubmit.link)
            assertEquals(sampleAvatarImage, afterSubmit.avatarImage)
        }
    }

    @Test
    fun editing_a_field_after_a_failed_submit_clears_only_that_fields_error() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = presenter,
        ) {
            uiStates.awaitItem()

            send(ProfileCardScreenAction.Submit)
            uiStates.awaitItem()

            send(ProfileCardScreenAction.UpdateNickName("droidkaigi"))
            val afterEdit = assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem())
            assertNull(afterEdit.nickNameErrorMessage)
            assertNotNull(afterEdit.occupationErrorMessage)
        }
    }

    @Test
    fun flip_card_toggles_which_face_is_showing() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = presenter,
        ) {
            uiStates.awaitItem()
            submitAValidForm()
            val front = assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem())
            assertEquals(false, front.isShowingBack)

            send(ProfileCardScreenAction.FlipCard)
            val back = assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem())
            assertEquals(true, back.isShowingBack)

            send(ProfileCardScreenAction.FlipCard)
            val frontAgain = assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem())
            assertEquals(false, frontAgain.isShowingBack)
        }
    }

    @Test
    fun edit_card_returns_to_the_form_showing_the_front_next_time() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = presenter,
        ) {
            uiStates.awaitItem()
            submitAValidForm()
            uiStates.awaitItem()
            send(ProfileCardScreenAction.FlipCard)
            uiStates.awaitItem()

            send(ProfileCardScreenAction.EditCard)
            assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem())

            // The form kept its still-valid field values across EditCard, so it can resubmit as-is.
            send(ProfileCardScreenAction.Submit)
            val afterResubmit = assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem())
            assertEquals(false, afterResubmit.isShowingBack)
        }
    }

    private suspend fun PresenterTestScope<ProfileCardScreenAction, Nothing, ProfileCardScreenUiState>.submitAValidForm() {
        send(ProfileCardScreenAction.UpdateNickName("droidkaigi"))
        uiStates.awaitItem()
        send(ProfileCardScreenAction.UpdateOccupation("Software Engineer"))
        uiStates.awaitItem()
        send(ProfileCardScreenAction.UpdateLink("https://example.com/user"))
        uiStates.awaitItem()
        send(ProfileCardScreenAction.UpdateAvatarImage(sampleAvatarImage))
        uiStates.awaitItem()
        send(ProfileCardScreenAction.Submit)
    }
}
