package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.ui.DeviceTilt
import io.github.droidkaigi.confsched.core.ui.DeviceTiltOverrideSource
import io.github.droidkaigi.confsched.core.ui.NoopDeviceTiltOverrideSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [NoopDeviceTiltOverrideSource::class])
class KaigiTiltOverrideStore : DeviceTiltOverrideSource {
    override val tilt: StateFlow<DeviceTilt?>
        field = MutableStateFlow(null)

    fun set(target: DeviceTilt) {
        tilt.value = target
    }

    fun reset() {
        tilt.value = null
    }
}
