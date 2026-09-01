package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.FirstFavoriteGuidanceConsumedSubscriptionKey
import io.github.droidkaigi.confsched.core.model.SoilIds
import soil.query.buildSubscriptionKey

@Inject
@ContributesBinding(AppScope::class)
class DefaultFirstFavoriteGuidanceConsumedSubscriptionKey(private val store: FirstFavoriteGuidanceStore) :
    FirstFavoriteGuidanceConsumedSubscriptionKey by buildSubscriptionKey(
        id = SoilIds.firstFavoriteGuidanceConsumedSubscription,
        subscribe = { store.consumed() },
    )
