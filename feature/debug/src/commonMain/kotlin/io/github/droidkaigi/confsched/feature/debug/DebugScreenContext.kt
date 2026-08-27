package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.data.PersistedDataResetter
import io.github.droidkaigi.confsched.core.model.DebugScreenScope
import io.github.droidkaigi.confsched.core.model.buildconfig.BuildConfigProvider

@Inject
class DebugPresenterContext(
    val buildConfig: BuildConfigProvider,
    val persistedDataResetter: PersistedDataResetter,
    val debugPreferencesStore: DebugPreferencesStore,
    val soilErrorMonitor: DebugSoilErrorMonitor,
    @SoilErrorOverlayEnabled val soilErrorOverlayEnabledMutationKey: SoilErrorOverlayEnabledMutationKey,
    val clock: KaigiClock,
    val clockOffsetStore: KaigiClockOffsetStore,
    @ClockOverlayEnabled val clockOverlayEnabledMutationKey: ClockOverlayEnabledMutationKey,
    override val logger: KaigiLogger,
) : PresenterContext

@Inject
@SingleIn(DebugScreenScope::class)
class DebugScreenContext(
    override val logger: KaigiLogger,
    val presenterContext: DebugPresenterContext,
) : ScreenContext
