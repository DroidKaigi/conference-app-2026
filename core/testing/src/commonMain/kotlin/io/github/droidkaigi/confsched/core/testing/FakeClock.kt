package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration
import kotlin.time.Instant

@Inject
@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class)
class FakeClock : KaigiClock {
    // A test that never sets the time still reads an instant during the event.
    var instant: Instant = DroidKaigi2026Day.Day1.at(hour = 10, minute = 0)

    override fun now(): Instant = instant

    override val offset: StateFlow<Duration> = MutableStateFlow(Duration.ZERO)

    fun advanceBy(duration: Duration) {
        instant += duration
    }
}
