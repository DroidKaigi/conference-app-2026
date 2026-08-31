package io.github.droidkaigi.confsched.feature.doodle.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.AboutHeroStageTopInset
import io.github.droidkaigi.confsched.core.ui.AboutHeroStageWidth
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardBack
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardFront
import io.github.droidkaigi.confsched.core.ui.rememberAboutHeroStage
import io.github.droidkaigi.confsched.feature.doodle.PlaceholderProfileCard
import io.github.droidkaigi.confsched.feature.doodle.referenceSize

/**
 * The surface being drawn on, shown behind the strokes as the hint of where they will land: the
 * About hero's stage art, or the card face itself. [scale] is the factor the canvas laid its own
 * space out at.
 */
@Composable
internal fun DoodleUnderlayView(
    target: DoodleTarget,
    card: ProfileCard?,
    scale: Float,
    modifier: Modifier = Modifier,
) {
    val face = card ?: PlaceholderProfileCard
    when (target) {
        DoodleTarget.AboutWall -> Box(modifier = modifier.background(MaterialTheme.colorScheme.primary)) {
            Image(
                imageVector = rememberAboutHeroStage(),
                contentDescription = null,
                alpha = STAGE_HINT_ALPHA,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = AboutHeroStageTopInset * scale)
                    .fillMaxWidth()
                    .widthIn(max = AboutHeroStageWidth * scale),
            )
        }

        DoodleTarget.ProfileCardFront -> ProfileCardFront(
            nickName = face.nickName,
            occupation = face.occupation,
            mascot = face.mascot,
            sketchiness = face.sketchiness,
            avatarImage = face.avatarImage,
            doodle = Doodle.Empty,
            taped = false,
            modifier = modifier,
        )

        DoodleTarget.ProfileCardBack -> ProfileCardBack(
            nickName = face.nickName,
            link = face.link,
            mascot = face.mascot,
            sketchiness = face.sketchiness,
            doodle = Doodle.Empty,
            taped = false,
            modifier = modifier,
        )
    }
}

private const val STAGE_HINT_ALPHA = 0.35f

@LocalePreviews
@Composable
private fun DoodleUnderlayViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleUnderlayView(
            target = DoodleTarget.AboutWall,
            card = null,
            scale = 1f,
            modifier = Modifier.size(DoodleTarget.AboutWall.referenceSize),
        )
    }
}

@LocalePreviews
@Composable
private fun DoodleUnderlayViewCardBackPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        DoodleUnderlayView(
            target = DoodleTarget.ProfileCardBack,
            card = PlaceholderProfileCard,
            scale = 1f,
            modifier = Modifier.size(DoodleTarget.ProfileCardBack.referenceSize),
        )
    }
}
