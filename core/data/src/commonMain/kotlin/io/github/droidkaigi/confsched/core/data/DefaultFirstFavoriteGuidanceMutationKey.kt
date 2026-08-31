package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.FirstFavoriteGuidanceMutationKey
import io.github.droidkaigi.confsched.core.model.FirstFavoriteNotificationScreenScope
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.SoilIds
import soil.query.buildMutationKey

@Inject
@ContributesBinding(FirstFavoriteNotificationScreenScope::class)
class DefaultFirstFavoriteGuidanceMutationKey(
    extraTag: MutationTag,
    private val store: FirstFavoriteGuidanceStore,
) : FirstFavoriteGuidanceMutationKey by buildMutationKey(
    id = SoilIds.firstFavoriteGuidanceMutation(extraTag),
    mutate = { store.consume() },
)
