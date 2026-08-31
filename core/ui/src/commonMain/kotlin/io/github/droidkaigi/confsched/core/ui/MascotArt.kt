package io.github.droidkaigi.confsched.core.ui

import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_mascot_a
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_mascot_b
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_mascot_c
import io.github.droidkaigi.confsched.core.ui.generated.resources.card_mascot_e
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_f
import org.jetbrains.compose.resources.DrawableResource

/** The single render of [Mascot.F], shared by every surface that draws the character. */
val mascotFArt: DrawableResource get() = Res.drawable.mascot_f

/** The card render of a [Mascot], or null for a character the design never draws on cards. */
internal val Mascot.cardArt: DrawableResource?
    get() = when (this) {
        Mascot.A -> Res.drawable.card_mascot_a
        Mascot.B -> Res.drawable.card_mascot_b
        Mascot.C -> Res.drawable.card_mascot_c
        Mascot.D -> null
        Mascot.E -> Res.drawable.card_mascot_e
        Mascot.F -> mascotFArt
    }
