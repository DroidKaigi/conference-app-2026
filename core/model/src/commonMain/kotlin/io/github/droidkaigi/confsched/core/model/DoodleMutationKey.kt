package io.github.droidkaigi.confsched.core.model

import soil.query.MutationKey

/** The drawing a user saves for one target; an empty [doodle] drops the target's drawing. */
data class DoodleEdit(val target: DoodleTarget, val doodle: Doodle)

/**
 * Saves a list of edits as one write, so a screen editing more than one target at a time reports a
 * single success or failure rather than one per target.
 */
typealias DoodleMutationKey = MutationKey<Unit, List<DoodleEdit>>
