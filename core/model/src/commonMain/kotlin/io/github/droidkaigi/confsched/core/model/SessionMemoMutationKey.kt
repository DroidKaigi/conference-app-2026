package io.github.droidkaigi.confsched.core.model

import soil.query.MutationKey

/** The note a user keeps against one session; an empty [text] drops the note. */
data class SessionMemoEdit(val id: TimetableItemId, val text: String)

typealias SessionMemoMutationKey = MutationKey<Unit, SessionMemoEdit>
