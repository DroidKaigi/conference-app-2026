package io.github.droidkaigi.confsched.feature.sessions.timetable

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.model.MutationTag
import io.github.droidkaigi.confsched.core.model.TimetableItemDetailScreenScope
import io.github.droidkaigi.confsched.core.model.TimetableItemId

@GraphExtension(TimetableItemDetailScreenScope::class)
interface TimetableItemDetailScreenGraph {
    val screenContext: TimetableItemDetailScreenContext
    val screenNavigator: TimetableItemDetailScreenNavigator

    @Provides
    private fun provideMutationTag(): MutationTag = MutationTag("TimetableItemDetailScreen")

    @GraphExtension.Factory
    @ContributesTo(UiScope::class)
    fun interface Factory {
        fun createTimetableItemDetailScreenGraph(@Provides timetableItemId: TimetableItemId): TimetableItemDetailScreenGraph
    }
}
