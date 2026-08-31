package io.github.droidkaigi.confsched.feature.doodle

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.MutationSuccessEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.ProfileCard
import kotlinx.collections.immutable.PersistentMap
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: DoodlePresenterContext)
fun doodleScreenPresenter(
    screenChannel: ScreenChannel<DoodleScreenAction, DoodleScreenActionResult>,
    savedDoodles: PersistentMap<DoodleTarget, Doodle>,
    card: ProfileCard?,
): DoodleScreenUiState {
    val doodleMutation = rememberMutation(presenterContext.doodleMutationKey)

    ActionEffect(screenChannel) { action ->
        when (action) {
            is DoodleScreenAction.SaveWall -> doodleMutation.mutateAsync(
                listOf(DoodleEdit(target = DoodleTarget.AboutWall, doodle = action.doodle)),
            )

            is DoodleScreenAction.SaveCard -> doodleMutation.mutateAsync(
                listOf(
                    DoodleEdit(target = DoodleTarget.ProfileCardFront, doodle = action.frontDoodle),
                    DoodleEdit(target = DoodleTarget.ProfileCardBack, doodle = action.backDoodle),
                ),
            )
        }
    }

    MutationSuccessEffect(doodleMutation) {
        screenChannel.emit(DoodleScreenActionResult.Saved)
        doodleMutation.reset()
    }
    MutationErrorEffect(doodleMutation) { error ->
        screenChannel.emit(DoodleScreenActionResult.ShowMessage(error.toUserMessage()))
        doodleMutation.reset()
    }

    return when (val target = presenterContext.target) {
        DoodleTarget.AboutWall -> DoodleScreenUiState.Wall(
            savedDoodle = savedDoodles[DoodleTarget.AboutWall] ?: Doodle.Empty,
            isSaving = doodleMutation.isPending,
        )

        DoodleTarget.ProfileCardFront, DoodleTarget.ProfileCardBack -> DoodleScreenUiState.Card(
            frontDoodle = savedDoodles[DoodleTarget.ProfileCardFront] ?: Doodle.Empty,
            backDoodle = savedDoodles[DoodleTarget.ProfileCardBack] ?: Doodle.Empty,
            initialFace = if (target == DoodleTarget.ProfileCardBack) DoodleCardFace.Back else DoodleCardFace.Front,
            card = card,
            isSaving = doodleMutation.isPending,
        )
    }
}
