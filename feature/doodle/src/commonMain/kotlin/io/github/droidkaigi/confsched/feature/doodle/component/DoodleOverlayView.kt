package io.github.droidkaigi.confsched.feature.doodle.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardBackQrPlateView
import io.github.droidkaigi.confsched.feature.doodle.PlaceholderProfileCard
import io.github.droidkaigi.confsched.feature.doodle.referenceSize

/**
 * What the target keeps above the strokes: the card back's QR plate, so a stroke reads as passing
 * under the code rather than covering it. Every other target draws nothing here.
 */
@Composable
internal fun DoodleOverlayView(
    target: DoodleTarget,
    card: ProfileCard?,
    scale: Float,
    modifier: Modifier = Modifier,
) {
    if (target != DoodleTarget.ProfileCardBack) return
    val face = card ?: PlaceholderProfileCard
    ProfileCardBackQrPlateView(
        nickName = face.nickName,
        link = face.link,
        sketchiness = face.sketchiness,
        scale = scale,
        modifier = modifier,
    )
}

@LocalePreviews
@Composable
private fun DoodleOverlayViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleOverlayView(
            target = DoodleTarget.ProfileCardBack,
            card = PlaceholderProfileCard,
            scale = 1f,
            modifier = Modifier.size(DoodleTarget.ProfileCardBack.referenceSize),
        )
    }
}
