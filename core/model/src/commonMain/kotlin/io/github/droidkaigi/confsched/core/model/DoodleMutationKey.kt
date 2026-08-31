package io.github.droidkaigi.confsched.core.model

import soil.query.MutationKey

/** The drawing a user saves for one target; an empty [doodle] drops the target's drawing. */
data class DoodleEdit(val target: DoodleTarget, val doodle: Doodle)

typealias DoodleMutationKey = MutationKey<Unit, DoodleEdit>
