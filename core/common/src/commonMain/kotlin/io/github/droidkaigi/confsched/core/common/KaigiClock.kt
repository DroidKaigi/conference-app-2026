package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * The app's source of the current time. The debug feature module replaces the binding with one the
 * debug tooling can shift.
 */
interface KaigiClock {
    fun now(): Instant
    /** Distance between [now] and the device clock; always zero outside debug builds. */
    val offset: StateFlow<Duration>
}

@Composable
fun KaigiClock.rememberCurrentTime(refreshInterval: Duration = 1.minutes): Instant {
    val clock = this
    val currentTime by produceState(clock.now(), clock) {
        clock.offset.collectLatest {
            while (true) {
                val now = clock.now()
                value = now
                val intervalMillis = refreshInterval.inWholeMilliseconds
                delay((intervalMillis - now.toEpochMilliseconds() % intervalMillis).milliseconds)
            }
        }
    }
    return currentTime
}

@Inject
@ContributesBinding(AppScope::class)
class SystemKaigiClock : KaigiClock {
    override fun now(): Instant = Clock.System.now()
    override val offset: StateFlow<Duration> = MutableStateFlow(Duration.ZERO)
}
