package io.github.droidkaigi.confsched.feature.debug

import com.kitakkun.jetwhale.agent.sdk.JetWhaleAgentPlugin
import com.kitakkun.jetwhale.protocol.messaging.JetWhaleMessageHandlers
import com.kitakkun.jetwhale.protocol.messaging.reply
import com.kitakkun.jetwhale.protocol.messaging.trySend
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiClock
import io.github.droidkaigi.confsched.core.ui.DeviceTilt
import io.github.droidkaigi.confsched.jetwhale.protocol.GetKaigiClockState
import io.github.droidkaigi.confsched.jetwhale.protocol.GetKaigiTiltState
import io.github.droidkaigi.confsched.jetwhale.protocol.KAIGI_PLUGIN_ID
import io.github.droidkaigi.confsched.jetwhale.protocol.KaigiClockChanged
import io.github.droidkaigi.confsched.jetwhale.protocol.KaigiClockPreset
import io.github.droidkaigi.confsched.jetwhale.protocol.KaigiClockState
import io.github.droidkaigi.confsched.jetwhale.protocol.KaigiTiltState
import io.github.droidkaigi.confsched.jetwhale.protocol.ResetKaigiClock
import io.github.droidkaigi.confsched.jetwhale.protocol.ResetKaigiTilt
import io.github.droidkaigi.confsched.jetwhale.protocol.SetKaigiTilt
import io.github.droidkaigi.confsched.jetwhale.protocol.ShiftKaigiClockTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
class KaigiAgentPlugin(
    private val clock: KaigiClock,
    private val offsetStore: KaigiClockOffsetStore,
    private val tiltOverrideStore: KaigiTiltOverrideStore,
) : JetWhaleAgentPlugin() {
    override val pluginId: String get() = KAIGI_PLUGIN_ID
    override val pluginVersion: String get() = "1.1.0"

    private val pluginScope = CoroutineScope(SupervisorJob())
    private var offsetJob: Job? = null

    override fun JetWhaleMessageHandlers.configure() {
        onRequest { _: GetKaigiClockState -> reply(currentState()) }

        onRequest { request: ShiftKaigiClockTo ->
            offsetStore.shiftTo(Instant.fromEpochMilliseconds(request.targetEpochMillis))
            reply(currentState())
        }

        onRequest { _: ResetKaigiClock ->
            offsetStore.reset()
            reply(currentState())
        }

        onRequest { _: GetKaigiTiltState -> reply(currentTiltState()) }

        onRequest { request: SetKaigiTilt ->
            tiltOverrideStore.set(DeviceTilt(request.pitchDegrees, request.rollDegrees))
            reply(currentTiltState())
        }

        onRequest { _: ResetKaigiTilt ->
            tiltOverrideStore.reset()
            reply(currentTiltState())
        }
    }

    // Mirrors shifts made on the debug screen back to the host, so the two stay in sync. The runtime
    // does not cancel a plugin's own coroutines, so an activation without a matching deactivation
    // would otherwise leave a second collector reporting every change twice.
    override fun onActivate() {
        offsetJob?.cancel()
        offsetJob = pluginScope.launch {
            offsetStore.offset.drop(1).collect {
                messenger.trySend(KaigiClockChanged(currentState()))
            }
        }
    }

    override fun onDeactivate() {
        offsetJob?.cancel()
        offsetJob = null
    }

    private fun currentState(): KaigiClockState = KaigiClockState(
        nowEpochMillis = clock.now().toEpochMilliseconds(),
        offsetMillis = offsetStore.offset.value.inWholeMilliseconds,
        presets = DebugClockPreset.entries.map {
            KaigiClockPreset(label = it.label, epochMillis = it.instant.toEpochMilliseconds())
        },
    )

    private fun currentTiltState(): KaigiTiltState {
        val tilt = tiltOverrideStore.tilt.value
        return KaigiTiltState(
            overridden = tilt != null,
            pitchDegrees = tilt?.pitchDegrees ?: 0f,
            rollDegrees = tilt?.rollDegrees ?: 0f,
        )
    }
}
