package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodlePenSize
import io.github.droidkaigi.confsched.core.model.DoodleStroke
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.Sketchiness
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fakeOnCardFace
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.DoodleCanvasView
import io.github.droidkaigi.confsched.core.ui.DoodleOrigin
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardBack
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardBackQrPlateView
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardColors
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardFaceDefaults
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardFront

/**
 * One card face turned into a drawing surface: the face itself is the hint of where the strokes
 * will land, and the back's QR plate stays above them so a stroke reads as passing under the code.
 */
@Composable
internal fun ProfileCardDoodleCanvasView(
    nickName: String,
    occupation: String,
    link: String,
    mascot: Mascot,
    sketchiness: Sketchiness,
    avatarImage: AvatarImage?,
    showsBack: Boolean,
    doodle: Doodle,
    penSize: DoodlePenSize,
    onStrokeAdd: (DoodleStroke) -> Unit,
    modifier: Modifier = Modifier,
) {
    DoodleCanvasView(
        doodle = doodle,
        referenceSize = ProfileCardFaceDefaults.size,
        maxScale = ProfileCardFaceDefaults.maxScale,
        origin = DoodleOrigin.TopStart,
        inkColor = ProfileCardColors.ink,
        haloColor = ProfileCardColors.plate,
        penSize = penSize,
        onStrokeAdd = onStrokeAdd,
        modifier = modifier,
        background = {
            if (showsBack) {
                ProfileCardBack(
                    nickName = nickName,
                    link = link,
                    mascot = mascot,
                    sketchiness = sketchiness,
                    doodle = Doodle.Empty,
                    taped = false,
                    modifier = Modifier.matchParentSize(),
                )
            } else {
                ProfileCardFront(
                    nickName = nickName,
                    occupation = occupation,
                    mascot = mascot,
                    sketchiness = sketchiness,
                    avatarImage = avatarImage,
                    doodle = Doodle.Empty,
                    taped = false,
                    modifier = Modifier.matchParentSize(),
                )
            }
        },
    ) { scale ->
        if (showsBack) {
            ProfileCardBackQrPlateView(
                nickName = nickName,
                link = link,
                sketchiness = sketchiness,
                scale = scale,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}

@LocalePreviews
@Composable
private fun ProfileCardDoodleCanvasViewPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardDoodleCanvasView(
            nickName = "Speaker A",
            occupation = "Software Engineer",
            link = "https://example.com/user",
            mascot = ProfileCard.DefaultMascot,
            sketchiness = ProfileCard.DefaultSketchiness,
            avatarImage = null,
            showsBack = false,
            doodle = Doodle.fakeOnCardFace(),
            penSize = DoodlePenSize.Normal,
            onStrokeAdd = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@LocalePreviews
@Composable
private fun ProfileCardDoodleCanvasViewBackPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ProfileCardDoodleCanvasView(
            nickName = "Speaker A",
            occupation = "Software Engineer",
            link = "https://example.com/user",
            mascot = ProfileCard.DefaultMascot,
            sketchiness = ProfileCard.DefaultSketchiness,
            avatarImage = null,
            showsBack = true,
            doodle = Doodle.fakeOnCardFace(),
            penSize = DoodlePenSize.Normal,
            onStrokeAdd = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
