package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.ProfileCardMutationKey
import io.github.droidkaigi.confsched.core.model.ProfileCardScreenScope
import io.github.droidkaigi.confsched.core.model.SoilIds
import kotlinx.coroutines.flow.first
import soil.query.buildMutationKey

@Inject
@ContributesBinding(ProfileCardScreenScope::class)
class DefaultProfileCardMutationKey(
    extraTag: MutationTag,
    store: ProfileCardDataStore,
    imageStore: ProfileImageStore,
) : ProfileCardMutationKey by buildMutationKey(
    id = SoilIds.profileCardMutation(extraTag),
    mutate = { card ->
        val previousPath = store.card().first()?.avatarImagePath
        val path = card.avatarImage?.let { imageStore.save(it.bytes) }
        store.save(card, path)
        if (previousPath != null && previousPath != path) imageStore.delete(previousPath)
    },
)
