package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.FirstFavoriteNotificationScreenScope
import io.github.droidkaigi.confsched.feature.favorites.FirstFavoriteNotificationScreenNavigator
import io.github.droidkaigi.confsched.feature.favorites.FirstFavoriteWidgetNavKey

@Inject
@SingleIn(FirstFavoriteNotificationScreenScope::class)
@ContributesBinding(
    scope = FirstFavoriteNotificationScreenScope::class,
    binding = binding<FirstFavoriteNotificationScreenNavigator>(),
)
class DefaultFirstFavoriteNotificationScreenNavigator(
    private val appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator),
    FirstFavoriteNotificationScreenNavigator {
    override fun openWidgetStep() {
        appNavigator.replaceTop(FirstFavoriteWidgetNavKey)
    }
}
