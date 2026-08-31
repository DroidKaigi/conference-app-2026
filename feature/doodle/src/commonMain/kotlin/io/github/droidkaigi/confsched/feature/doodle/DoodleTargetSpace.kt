package io.github.droidkaigi.confsched.feature.doodle

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.ui.AboutHeroSize
import io.github.droidkaigi.confsched.core.ui.DoodleOrigin
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardColors
import io.github.droidkaigi.confsched.core.ui.profilecard.ProfileCardFaceDefaults

/** The dp space a target's drawing is stored in. */
internal val DoodleTarget.referenceSize: DpSize
    get() = when (this) {
        DoodleTarget.AboutWall -> AboutHeroSize
        DoodleTarget.ProfileCardFront, DoodleTarget.ProfileCardBack -> ProfileCardFaceDefaults.size
    }

internal val DoodleTarget.doodleOrigin: DoodleOrigin
    get() = when (this) {
        DoodleTarget.AboutWall -> DoodleOrigin.TopCenter
        DoodleTarget.ProfileCardFront, DoodleTarget.ProfileCardBack -> DoodleOrigin.TopStart
    }

internal val DoodleTarget.inkColor: Color
    @Composable get() = when (this) {
        DoodleTarget.AboutWall -> MaterialTheme.colorScheme.onPrimary
        DoodleTarget.ProfileCardFront, DoodleTarget.ProfileCardBack -> ProfileCardColors.ink
    }

internal val DoodleTarget.haloColor: Color?
    @Composable get() = when (this) {
        DoodleTarget.AboutWall -> null
        DoodleTarget.ProfileCardFront, DoodleTarget.ProfileCardBack -> ProfileCardColors.plate
    }

/**
 * The face an underlay falls back to before the user has created their card. The card targets are
 * unreachable then, so this only keeps the screen rendering rather than standing in for real data.
 */
internal val PlaceholderProfileCard = ProfileCard(
    nickName = "",
    occupation = "",
    link = "",
    mascot = ProfileCard.DefaultMascot,
    sketchiness = ProfileCard.DefaultSketchiness,
    avatarImage = null,
)
