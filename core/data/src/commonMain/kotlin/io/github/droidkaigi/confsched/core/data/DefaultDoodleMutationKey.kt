package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.AboutScreenScope
import io.github.droidkaigi.confsched.core.model.DoodleMutationKey
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.ProfileCardScreenScope
import io.github.droidkaigi.confsched.core.model.SoilIds
import soil.query.buildMutationKey

@Inject
@ContributesBinding(AboutScreenScope::class)
@ContributesBinding(ProfileCardScreenScope::class)
class DefaultDoodleMutationKey(
    extraTag: MutationTag,
    private val store: DoodleStore,
) : DoodleMutationKey by buildMutationKey(
    id = SoilIds.doodleMutation(extraTag),
    mutate = { edits -> edits.forEach { edit -> store.save(edit.target, edit.doodle) } },
)
