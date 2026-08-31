package io.github.droidkaigi.confsched.feature.doodle

import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.ProfileCard

sealed interface DoodleScreenUiState {
    val isSaving: Boolean

    data class Wall(
        val savedDoodle: Doodle,
        override val isSaving: Boolean,
    ) : DoodleScreenUiState

    /**
     * Both card faces at once: whichever face the screen opens on, the other one is edited in the
     * same visit and a save writes the pair.
     */
    data class Card(
        val frontDoodle: Doodle,
        val backDoodle: Doodle,
        val initialFace: DoodleCardFace,
        /** Null while the user has not created their card; the card targets are then unreachable. */
        val card: ProfileCard?,
        override val isSaving: Boolean,
    ) : DoodleScreenUiState
}
