package io.github.droidkaigi.confsched.feature.sessions.timetable

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableIdsSubscriptionKey
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableItemIdMutationKey
import io.github.droidkaigi.confsched.core.model.SessionMemoMutationKey
import io.github.droidkaigi.confsched.core.model.SessionMemosSubscriptionKey
import io.github.droidkaigi.confsched.core.model.TimetableItemDetailScreenScope
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableQueryKey

@Inject
class TimetableItemDetailPresenterContext(
    val favoriteTimetableItemIdMutationKey: FavoriteTimetableItemIdMutationKey,
    val sessionMemoMutationKey: SessionMemoMutationKey,
) : PresenterContext

@Inject
@SingleIn(TimetableItemDetailScreenScope::class)
class TimetableItemDetailScreenContext(
    val timetableItemId: TimetableItemId,
    val timetableQueryKey: TimetableQueryKey,
    val favoriteTimetableIdsSubscriptionKey: FavoriteTimetableIdsSubscriptionKey,
    val sessionMemosSubscriptionKey: SessionMemosSubscriptionKey,
    val presenterContext: TimetableItemDetailPresenterContext,
) : ScreenContext
