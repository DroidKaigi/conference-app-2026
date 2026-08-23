package io.github.droidkaigi.confsched.feature.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.ClockOverlay
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.common.NoopClockOverlay
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [NoopClockOverlay::class])
class DebugClockOverlay(
    private val clock: KaigiClock,
    private val debugPreferencesStore: DebugPreferencesStore,
) : ClockOverlay {

    @Composable
    override fun Overlay() {
        val enabled by debugPreferencesStore.clockOverlayEnabled.collectAsState(initial = true)
        val offset by clock.offset.collectAsState()
        // An unshifted clock has nothing to warn about, so the badge appears exactly while the app
        // is reading a time the device does not.
        if (!enabled || offset == Duration.ZERO) return

        var now by remember { mutableStateOf(clock.now()) }
        LaunchedEffect(offset) {
            while (true) {
                now = clock.now()
                delay(1.seconds)
            }
        }

        ShiftedClockBadge(now = now.formatInConferenceTime(), offsetLabel = offset.toOffsetLabel())
    }
}
