package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.AppearanceSettingsSubscriptionKey
import io.github.droidkaigi.confsched.core.model.SoilIds
import soil.query.buildSubscriptionKey

@Inject
@ContributesBinding(AppScope::class)
class DefaultAppearanceSettingsSubscriptionKey(private val store: AppearanceSettingsStore) :
    AppearanceSettingsSubscriptionKey by buildSubscriptionKey(
        id = SoilIds.appearanceSettingsSubscription,
        subscribe = { store.settings() },
    )
