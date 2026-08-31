package io.github.droidkaigi.confsched.feature.doodle

import io.github.droidkaigi.confsched.core.model.DoodleTarget

/** One of the two profile card faces a doodle can land on, and the target holding that face's own drawing. */
enum class DoodleCardFace(val target: DoodleTarget) {
    Front(DoodleTarget.ProfileCardFront),
    Back(DoodleTarget.ProfileCardBack),
}
