package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.FavoritesScreenScope
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.favorites.FavoritesScreenNavigator
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey

@Inject
@SingleIn(FavoritesScreenScope::class)
@ContributesBinding(scope = FavoritesScreenScope::class,
    binding = binding<FavoritesScreenNavigator>()
)
class DefaultFavoritesScreenNavigator(
    private val appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator), FavoritesScreenNavigator {
    override fun openSessionDetail(id: TimetableItemId) {
        appNavigator.goTo(TimetableItemDetailNavKey(id))
    }
}
