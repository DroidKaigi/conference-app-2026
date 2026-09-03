package io.github.droidkaigi.confsched.feature.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import io.github.droidkaigi.confsched.core.common.ActionEffect
import io.github.droidkaigi.confsched.core.common.MutationErrorEffect
import io.github.droidkaigi.confsched.core.common.MutationSuccessEffect
import io.github.droidkaigi.confsched.core.common.ScreenChannel
import io.github.droidkaigi.confsched.core.common.toUserMessage
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import soil.query.compose.rememberMutation

@Composable
context(presenterContext: AboutPresenterContext)
fun aboutScreenPresenter(
    screenChannel: ScreenChannel<AboutScreenAction, AboutScreenActionResult>,
    doodle: Doodle,
): AboutScreenUiState {
    val doodleMutation = rememberMutation(presenterContext.doodleMutationKey)
    var isDoodlingWall by retain { mutableStateOf(false) }

    ActionEffect(screenChannel) { action ->
        when (action) {
            AboutScreenAction.StartDoodling -> isDoodlingWall = true

            AboutScreenAction.CancelDoodling -> isDoodlingWall = false

            is AboutScreenAction.SaveWallDoodle -> doodleMutation.mutateAsync(
                listOf(DoodleEdit(target = DoodleTarget.AboutWall, doodle = action.doodle)),
            )
        }
    }

    MutationSuccessEffect(doodleMutation) {
        isDoodlingWall = false
        doodleMutation.reset()
    }
    MutationErrorEffect(doodleMutation) { error ->
        screenChannel.emit(AboutScreenActionResult.ShowMessage(error.toUserMessage()))
        doodleMutation.reset()
    }

    return AboutScreenUiState(
        versionName = presenterContext.buildConfig.versionName,
        doodle = doodle,
        isDoodlingWall = isDoodlingWall,
    )
}
