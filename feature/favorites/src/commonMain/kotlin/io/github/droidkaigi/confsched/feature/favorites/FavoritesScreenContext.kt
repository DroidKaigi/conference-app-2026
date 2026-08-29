package io.github.droidkaigi.confsched.feature.favorites

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableIdsSubscriptionKey
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableItemIdMutationKey
import io.github.droidkaigi.confsched.core.model.FavoritesScreenScope
import io.github.droidkaigi.confsched.core.model.TimetableQueryKey

@Inject
class FavoritesPresenterContext(
    val favoriteTimetableItemIdMutationKey: FavoriteTimetableItemIdMutationKey,
    val clock: KaigiClock,
    override val logger: KaigiLogger,
) : PresenterContext

@Inject
@SingleIn(FavoritesScreenScope::class)
class FavoritesScreenContext(
    val timetableQueryKey: TimetableQueryKey,
    val favoriteTimetableIdsSubscriptionKey: FavoriteTimetableIdsSubscriptionKey,
    override val logger: KaigiLogger,
    val presenterContext: FavoritesPresenterContext,
) : ScreenContext
