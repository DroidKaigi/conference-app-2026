package io.github.droidkaigi.confsched.core.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.designsystem.icon.Flip
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_switch_to_back
import io.github.droidkaigi.confsched.core.ui.generated.resources.doodle_switch_to_front
import org.jetbrains.compose.resources.stringResource

/**
 * Turns a two-faced surface over while it is being drawn on; [isShowingBack] names the face in view.
 * While [enabled] is false the turn is withheld, which is what a stroke still under a finger needs.
 */
@Composable
fun DoodleFlipButton(
    isShowingBack: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val description = if (isShowingBack) {
        stringResource(Res.string.doodle_switch_to_front)
    } else {
        stringResource(Res.string.doodle_switch_to_back)
    }
    KaigiIconButton(
        seed = FLIP_BUTTON_SEED,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        size = KaigiButtonDefaults.height,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Icon(KaigiIcons.Default.Flip, contentDescription = description)
    }
}

private const val FLIP_BUTTON_SEED = 4351

@LocalePreviews
@Composable
private fun DoodleFlipButtonPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleFlipButton(isShowingBack = false, onClick = {}, enabled = true)
    }
}

@LocalePreviews
@Composable
private fun DoodleFlipButtonShowingBackPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleFlipButton(isShowingBack = true, onClick = {}, enabled = true)
    }
}
