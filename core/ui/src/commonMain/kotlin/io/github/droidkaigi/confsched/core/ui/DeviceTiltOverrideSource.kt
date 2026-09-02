package io.github.droidkaigi.confsched.core.ui

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A tilt the app reads in place of the platform sensor, or null to follow the sensor. The debug
 * feature module replaces the no-op binding with one the debug tooling can set.
 */
interface DeviceTiltOverrideSource {
    val tilt: StateFlow<DeviceTilt?>
}

@Inject
@ContributesBinding(AppScope::class)
class NoopDeviceTiltOverrideSource : DeviceTiltOverrideSource {
    override val tilt: StateFlow<DeviceTilt?> = MutableStateFlow(null)
}
