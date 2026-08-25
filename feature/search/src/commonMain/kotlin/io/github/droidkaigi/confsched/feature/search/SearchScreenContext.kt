package io.github.droidkaigi.confsched.feature.search

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableIdsSubscriptionKey
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableItemIdMutationKey
import io.github.droidkaigi.confsched.core.model.SearchScreenScope
import io.github.droidkaigi.confsched.core.model.TimetableQueryKey

@Inject
class SearchPresenterContext(
    val favoriteTimetableItemIdMutationKey: FavoriteTimetableItemIdMutationKey,
    val clock: KaigiClock,
) : PresenterContext

@Inject
@SingleIn(SearchScreenScope::class)
class SearchScreenContext(
    val timetableQueryKey: TimetableQueryKey,
    val favoriteTimetableIdsSubscriptionKey: FavoriteTimetableIdsSubscriptionKey,
    val presenterContext: SearchPresenterContext,
) : ScreenContext
