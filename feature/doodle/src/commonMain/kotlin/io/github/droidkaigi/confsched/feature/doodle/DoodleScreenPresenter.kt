package io.github.droidkaigi.confsched.feature.doodle

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.MutationSuccessEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.ProfileCard
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: DoodlePresenterContext)
fun doodleScreenPresenter(
    screenChannel: ScreenChannel<DoodleScreenAction, DoodleScreenActionResult>,
    savedDoodle: Doodle,
    card: ProfileCard?,
): DoodleScreenUiState {
    val doodleMutation = rememberMutation(presenterContext.doodleMutationKey)

    ActionEffect(screenChannel) { action ->
        when (action) {
            is DoodleScreenAction.Save -> doodleMutation.mutateAsync(
                DoodleEdit(target = presenterContext.target, doodle = action.doodle),
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

    return DoodleScreenUiState(
        target = presenterContext.target,
        savedDoodle = savedDoodle,
        card = card,
        isSaving = doodleMutation.isPending,
    )
}
