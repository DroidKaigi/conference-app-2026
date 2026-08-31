package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.DoodleSubscriptionKey
import io.github.droidkaigi.confsched.core.model.SoilIds
import soil.query.buildSubscriptionKey

@Inject
@ContributesBinding(AppScope::class)
class DefaultDoodleSubscriptionKey(private val store: DoodleStore) :
    DoodleSubscriptionKey by buildSubscriptionKey(
        id = SoilIds.doodleSubscription,
        subscribe = { store.doodle() },
    )
