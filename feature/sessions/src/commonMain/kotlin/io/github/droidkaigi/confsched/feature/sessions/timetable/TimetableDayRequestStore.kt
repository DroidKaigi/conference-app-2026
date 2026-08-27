package io.github.droidkaigi.confsched.feature.sessions.timetable

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Buffers the day a deep link asks the timetable to show until the screen is composed, and hands
 * it over once, so a day the reader picks afterwards stands.
 */
class TimetableDayRequestStore {
    private val channel = Channel<DroidKaigi2026Day>(Channel.CONFLATED)

    val requests: Flow<DroidKaigi2026Day> = channel.receiveAsFlow()

    fun request(day: DroidKaigi2026Day) {
        channel.trySend(day)
    }
}

@ContributesTo(AppScope::class)
interface TimetableDayRequestStoreBindings {
    @Provides
    @SingleIn(AppScope::class)
    fun provideTimetableDayRequestStore(): TimetableDayRequestStore = TimetableDayRequestStore()
}
