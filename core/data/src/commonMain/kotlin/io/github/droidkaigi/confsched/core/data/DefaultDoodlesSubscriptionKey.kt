package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.DoodlesSubscriptionKey
import io.github.droidkaigi.confsched.core.model.SoilIds
import soil.query.buildSubscriptionKey

@Inject
@ContributesBinding(AppScope::class)
class DefaultDoodlesSubscriptionKey(private val store: DoodleStore) :
    DoodlesSubscriptionKey by buildSubscriptionKey(
        id = SoilIds.doodlesSubscription,
        subscribe = { store.doodles() },
    )
