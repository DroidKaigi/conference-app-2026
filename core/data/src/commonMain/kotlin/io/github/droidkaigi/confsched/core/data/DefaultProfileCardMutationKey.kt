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
    profileCardStore: ProfileCardStore,
    avatarImageStore: AvatarImageStore,
) : ProfileCardMutationKey by buildMutationKey(
    id = SoilIds.profileCardMutation(extraTag),
    mutate = { card ->
        val previousPath = profileCardStore.card().first()?.avatarImagePath
        val path = card.avatarImage?.let { avatarImageStore.save(it.bytes) }
        profileCardStore.save(card, path)
        if (previousPath != null && path == null) avatarImageStore.delete(previousPath)
    },
)
