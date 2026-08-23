package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.AppearanceSubscriptionKey
import io.github.droidkaigi.confsched.core.model.SoilIds
import soil.query.buildSubscriptionKey

@Inject
@ContributesBinding(AppScope::class)
class DefaultAppearanceSubscriptionKey(private val store: AppearanceSettingsStore) :
    AppearanceSubscriptionKey by buildSubscriptionKey(
        id = SoilIds.appearanceSubscription,
        subscribe = { store.appearance() },
    )
