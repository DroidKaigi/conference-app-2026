package io.github.droidkaigi.confsched.feature.sessions.timetable

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableIdsSubscriptionKey
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableItemIdMutationKey
import io.github.droidkaigi.confsched.core.model.FirstFavoriteGuidanceConsumedSubscriptionKey
import io.github.droidkaigi.confsched.core.model.TimetableQueryKey
import io.github.droidkaigi.confsched.core.model.TimetableScreenScope

@Inject
class TimetablePresenterContext(
    val favoriteTimetableItemIdMutationKey: FavoriteTimetableItemIdMutationKey,
    val clock: KaigiClock,
    val dayRequestStore: TimetableDayRequestStore,
    override val logger: KaigiLogger,
) : PresenterContext

@Inject
@SingleIn(TimetableScreenScope::class)
class TimetableScreenContext(
    val timetableQueryKey: TimetableQueryKey,
    val favoriteTimetableIdsSubscriptionKey: FavoriteTimetableIdsSubscriptionKey,
    val firstFavoriteGuidanceConsumedSubscriptionKey: FirstFavoriteGuidanceConsumedSubscriptionKey,
    override val logger: KaigiLogger,
    val presenterContext: TimetablePresenterContext,
) : ScreenContext
