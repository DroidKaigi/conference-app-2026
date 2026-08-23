package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import soil.query.SubscriptionKey
import soil.query.buildSubscriptionKey

typealias ThemeColorSchemeSubscriptionKey = SubscriptionKey<KaigiColorScheme>

@Inject
@ContributesBinding(AppScope::class)
class DefaultThemeColorSchemeSubscriptionKey(private val store: AppearanceSettingsStore) :
    ThemeColorSchemeSubscriptionKey by buildSubscriptionKey(
        id = SoilIds.themeColorSchemeSubscription,
        subscribe = { store.colorScheme() },
    )
