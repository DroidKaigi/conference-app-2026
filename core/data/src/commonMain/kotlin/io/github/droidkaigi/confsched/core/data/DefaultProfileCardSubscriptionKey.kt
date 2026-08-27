package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.AvatarImage
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.ProfileCardSubscriptionKey
import io.github.droidkaigi.confsched.core.model.SoilIds
import kotlinx.coroutines.flow.map
import soil.query.buildSubscriptionKey

@Inject
@ContributesBinding(AppScope::class)
class DefaultProfileCardSubscriptionKey(
    profileCardStore: ProfileCardStore,
    avatarImageStore: AvatarImageStore,
) : ProfileCardSubscriptionKey by buildSubscriptionKey(
    id = SoilIds.profileCardSubscription,
    subscribe = {
        profileCardStore.card().map { stored ->
            stored?.let {
                ProfileCard(
                    nickName = it.nickName,
                    occupation = it.occupation,
                    link = it.link,
                    mascot = it.mascot,
                    sketchiness = it.sketchiness,
                    avatarImage = it.avatarImagePath?.let { path -> avatarImageStore.load(path) }?.let(::AvatarImage),
                )
            }
        }
    },
)
