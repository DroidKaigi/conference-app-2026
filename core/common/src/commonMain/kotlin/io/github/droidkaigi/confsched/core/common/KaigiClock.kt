package io.github.droidkaigi.confsched.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

/**
 * The app's source of the current time. The debug feature module replaces the binding with one the
 * debug tooling can shift.
 */
interface KaigiClock {
    fun now(): Instant
}

@Composable
fun KaigiClock.rememberCurrentTime(): Instant {
    var currentTime by retain { mutableStateOf(now()) }
    LaunchedEffect(this) {
        while (true) {
            val now = now()
            currentTime = now

            val millisPastMinute = now.toEpochMilliseconds() % 60_000L
            val delayMillis = 60_000L - millisPastMinute
            delay(delayMillis.milliseconds)
        }
    }
    return currentTime
}

@Inject
@ContributesBinding(AppScope::class)
class SystemKaigiClock : KaigiClock {
    override fun now(): Instant = Clock.System.now()
}
