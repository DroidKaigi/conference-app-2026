package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.ui.graphics.ImageBitmap
import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProfileCardScreenPresenterTest {

    private val graph = createGraph<ProfileCardScreenTestGraph>()

    private val storedCard = ProfileCard(
        nickName = "Speaker A",
        occupation = "Software Engineer",
        link = "https://example.com",
        mascot = Mascot.D,
        sketchiness = Sketchiness.Playful,
        avatarImage = AvatarImage(byteArrayOf(1, 2, 3)),
    )

    @Test
    fun the_form_is_shown_while_no_card_is_stored() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = null, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            assertEquals(ProfileCardScreenUiState.Form(), uiStates.awaitItem())
        }
    }

    @Test
    fun the_stored_card_is_shown_once_one_exists() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = storedCard, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            val uiState = assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem())
            assertEquals("Speaker A", uiState.nickName)
            assertEquals(Mascot.D, uiState.mascot)
            assertEquals(Sketchiness.Playful, uiState.sketchiness)
            assertEquals(storedCard.avatarImage, uiState.avatarImage)
        }
    }

    @Test
    fun each_face_carries_its_own_doodle() {
        val frontDoodle = Doodle.fakeOnCardFace()
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel ->
                profileCardScreenPresenter(
                    screenChannel = channel,
                    storedCard = storedCard,
                    frontDoodle = frontDoodle,
                    backDoodle = Doodle.Empty,
                )
            },
        ) {
            val uiState = assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem())
            assertEquals(frontDoodle, uiState.frontDoodle)
            assertEquals(Doodle.Empty, uiState.backDoodle)
        }
    }

    @Test
    fun editing_a_field_reaches_the_form() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = null, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.UpdateNickName("Speaker B"))
            assertEquals("Speaker B", assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem()).nickName)
        }
    }

    @Test
    fun submitting_writes_the_card_the_form_holds() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = null, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.UpdateNickName("Speaker B"))
            send(ProfileCardScreenAction.UpdateOccupation("Designer"))
            send(ProfileCardScreenAction.UpdateLink("https://example.com/b"))
            send(ProfileCardScreenAction.UpdateMascot(Mascot.E))
            send(ProfileCardScreenAction.UpdateSketchiness(Sketchiness.Subtle))
            send(ProfileCardScreenAction.UpdateAvatarImage(AvatarImage(byteArrayOf(4, 5))))
            send(ProfileCardScreenAction.Submit)
            assertEquals(
                ProfileCard(
                    nickName = "Speaker B",
                    occupation = "Designer",
                    link = "https://example.com/b",
                    mascot = Mascot.E,
                    sketchiness = Sketchiness.Subtle,
                    avatarImage = AvatarImage(byteArrayOf(4, 5)),
                ),
                graph.profileCardMutationKey.invocations.receive(),
            )
        }
    }

    @Test
    fun editing_a_stored_card_opens_the_form_prefilled() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = storedCard, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.EditCard)
            val uiState = assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem())
            assertEquals(
                ProfileCardScreenUiState.Form(
                    nickName = "Speaker A",
                    occupation = "Software Engineer",
                    link = "https://example.com",
                    mascot = Mascot.D,
                    sketchiness = Sketchiness.Playful,
                    avatarImage = storedCard.avatarImage,
                ),
                uiState,
            )
        }
    }

    @Test
    fun submitting_an_empty_form_reports_every_field_as_required_and_stays_on_the_form() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = null, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.Submit)
            val form = assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem())
            assertEquals(ProfileCardFormError.NickNameRequired, form.nickNameError)
            assertEquals(ProfileCardFormError.OccupationRequired, form.occupationError)
            assertEquals(ProfileCardFormError.LinkRequired, form.linkError)
            assertEquals(ProfileCardFormError.AvatarImageRequired, form.avatarImageError)
            assertTrue(graph.profileCardMutationKey.invocations.isEmpty)
        }
    }

    @Test
    fun submitting_a_link_that_is_not_an_http_url_reports_it_as_malformed() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = null, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.UpdateLink("example.com"))
            uiStates.awaitItem()
            send(ProfileCardScreenAction.Submit)
            assertEquals(ProfileCardFormError.LinkMalformed, assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem()).linkError)
        }
    }

    @Test
    fun editing_a_field_clears_only_that_fields_error() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = null, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.Submit)
            uiStates.awaitItem()
            send(ProfileCardScreenAction.UpdateNickName("Speaker A"))
            val form = assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem())
            assertNull(form.nickNameError)
            assertEquals(ProfileCardFormError.OccupationRequired, form.occupationError)
        }
    }

    @Test
    fun removing_the_picked_image_clears_it_and_leaves_it_required() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = null, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.UpdateAvatarImage(AvatarImage(byteArrayOf(4, 5))))
            uiStates.awaitItem()
            send(ProfileCardScreenAction.RemoveAvatarImage)
            assertNull(assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem()).avatarImage)
            send(ProfileCardScreenAction.Submit)
            val form = assertIs<ProfileCardScreenUiState.Form>(uiStates.awaitItem())
            assertEquals(ProfileCardFormError.AvatarImageRequired, form.avatarImageError)
            assertTrue(graph.profileCardMutationKey.invocations.isEmpty)
        }
    }

    @Test
    fun turning_the_card_over_swaps_the_face_it_shows() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = storedCard, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            assertFalse(assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem()).isShowingBack)
            send(ProfileCardScreenAction.FlipCard)
            assertTrue(assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem()).isShowingBack)
            send(ProfileCardScreenAction.FlipCard)
            assertFalse(assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem()).isShowingBack)
        }
    }

    @Test
    fun a_write_that_fails_reports_the_error() {
        graph.profileCardMutationKey.failWith(RuntimeException("the card could not be written"))
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = null, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.UpdateNickName("Speaker B"))
            send(ProfileCardScreenAction.UpdateOccupation("Designer"))
            send(ProfileCardScreenAction.UpdateLink("https://example.com/b"))
            send(ProfileCardScreenAction.UpdateAvatarImage(AvatarImage(byteArrayOf(4, 5))))
            send(ProfileCardScreenAction.Submit)
            assertIs<ProfileCardScreenActionResult.ShowMessage>(results.awaitItem())
        }
    }

    @Test
    fun sharing_the_card_hands_back_an_encoded_image() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = storedCard, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.Share(ImageBitmap(width = 1, height = 1)))
            val result = assertIs<ProfileCardScreenActionResult.ShareImage>(results.awaitItem())
            assertTrue(result.image.pngBytes.isNotEmpty())
        }
    }

    @Test
    fun finishing_a_doodle_writes_both_faces_and_leaves_the_mode() {
        val drawnFront = Doodle.fakeOnCardFace()
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = storedCard, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            assertFalse(assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem()).isDoodling)
            send(ProfileCardScreenAction.StartDoodling)
            assertTrue(assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem()).isDoodling)
            send(ProfileCardScreenAction.SaveDoodles(front = drawnFront, back = Doodle.Empty))
            assertEquals(
                listOf(
                    DoodleEdit(target = DoodleTarget.ProfileCardFront, doodle = drawnFront),
                    DoodleEdit(target = DoodleTarget.ProfileCardBack, doodle = Doodle.Empty),
                ),
                graph.doodleMutationKey.invocations.receive(),
            )
            assertFalse(assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem()).isDoodling)
        }
    }

    @Test
    fun a_doodle_save_that_fails_reports_the_error_and_stays_in_the_mode() {
        graph.doodleMutationKey.failWith(RuntimeException("the doodle could not be written"))
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = storedCard, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.StartDoodling)
            assertTrue(assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem()).isDoodling)
            send(ProfileCardScreenAction.SaveDoodles(front = Doodle.fakeOnCardFace(), back = Doodle.Empty))
            assertIs<ProfileCardScreenActionResult.ShowMessage>(results.awaitItem())
            // The mode is unchanged, so the presenter emits no further state.
            uiStates.expectNoEvents()
        }
    }

    @Test
    fun cancelling_a_doodle_leaves_the_mode_without_writing_anything() {
        runPresenterTest(
            presenterContext = graph.presenterContext,
            presenter = { channel -> profileCardScreenPresenter(screenChannel = channel, storedCard = storedCard, frontDoodle = Doodle.Empty, backDoodle = Doodle.Empty) },
        ) {
            uiStates.awaitItem()
            send(ProfileCardScreenAction.StartDoodling)
            assertTrue(assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem()).isDoodling)
            send(ProfileCardScreenAction.CancelDoodling)
            assertFalse(assertIs<ProfileCardScreenUiState.Card>(uiStates.awaitItem()).isDoodling)
            assertTrue(graph.doodleMutationKey.invocations.isEmpty)
        }
    }
}
