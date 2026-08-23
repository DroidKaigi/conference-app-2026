package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.SessionMemoMutationKey
import io.github.droidkaigi.confsched.core.model.SoilIds
import io.github.droidkaigi.confsched.core.model.TimetableItemDetailScreenScope
import soil.query.buildMutationKey

@Inject
@ContributesBinding(TimetableItemDetailScreenScope::class)
class DefaultSessionMemoMutationKey(
    extraTag: MutationTag,
    private val store: SessionMemoStore,
) : SessionMemoMutationKey by buildMutationKey(
    id = SoilIds.sessionMemoMutation(extraTag),
    mutate = { edit -> store.write(edit.id, edit.text) },
)
