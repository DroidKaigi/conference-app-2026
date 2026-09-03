package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.FirstFavoriteWidgetScreenScope
import io.github.droidkaigi.confsched.feature.favorites.FirstFavoriteWidgetScreenNavigator

@Inject
@SingleIn(FirstFavoriteWidgetScreenScope::class)
@ContributesBinding(
    scope = FirstFavoriteWidgetScreenScope::class,
    binding = binding<FirstFavoriteWidgetScreenNavigator>(),
)
class DefaultFirstFavoriteWidgetScreenNavigator(
    appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator),
    FirstFavoriteWidgetScreenNavigator
