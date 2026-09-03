package io.github.droidkaigi.confsched.jetwhale.host

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kitakkun.jetwhale.annotations.ExperimentalJetWhaleApi
import com.kitakkun.jetwhale.host.sdk.JetWhaleHostPlugin
import com.kitakkun.jetwhale.host.sdk.JetWhaleHostPluginFactory
import com.kitakkun.jetwhale.host.sdk.JetWhaleHostPluginUi
import com.kitakkun.jetwhale.host.sdk.JetWhaleMcpArgumentException
import com.kitakkun.jetwhale.host.sdk.JetWhaleMcpArguments
import com.kitakkun.jetwhale.host.sdk.JetWhaleMcpCapablePlugin
import com.kitakkun.jetwhale.host.sdk.JetWhaleMcpCommand
import com.kitakkun.jetwhale.host.sdk.JetWhaleMessagingHostPlugin
import com.kitakkun.jetwhale.protocol.messaging.JetWhaleMessageHandlers
import com.kitakkun.jetwhale.protocol.messaging.JetWhaleMessagingException
import com.kitakkun.jetwhale.protocol.messaging.request
import io.github.droidkaigi.confsched.jetwhale.protocol.GetKaigiClockState
import io.github.droidkaigi.confsched.jetwhale.protocol.GetKaigiTiltState
import io.github.droidkaigi.confsched.jetwhale.protocol.KaigiClockChanged
import io.github.droidkaigi.confsched.jetwhale.protocol.KaigiClockState
import io.github.droidkaigi.confsched.jetwhale.protocol.KaigiTiltState
import io.github.droidkaigi.confsched.jetwhale.protocol.ResetKaigiClock
import io.github.droidkaigi.confsched.jetwhale.protocol.ResetKaigiTilt
import io.github.droidkaigi.confsched.jetwhale.protocol.SetKaigiTilt
import io.github.droidkaigi.confsched.jetwhale.protocol.ShiftKaigiClockTo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.time.Instant

// Instantiated by the host via the fully-qualified name declared in plugin-manifest.json.
@Suppress("UNUSED")
class KaigiHostPluginFactory : JetWhaleHostPluginFactory {
    override fun createPlugin(): JetWhaleHostPlugin = KaigiHostPlugin()
}

@OptIn(ExperimentalJetWhaleApi::class)
private class KaigiHostPlugin :
    JetWhaleMessagingHostPlugin(),
    JetWhaleHostPluginUi,
    JetWhaleMcpCapablePlugin {

    private var clockState by mutableStateOf<KaigiClockState?>(null)
    private var tiltState by mutableStateOf<KaigiTiltState?>(null)
    private var lastError by mutableStateOf<String?>(null)
    private var pendingClockRequest: Job? = null
    private var pendingTiltRequest: Job? = null

    override fun JetWhaleMessageHandlers.configure() {
        onEvent { event: KaigiClockChanged -> clockState = event.state }
    }

    override suspend fun onPrepare() {
        clockState = messenger.request(GetKaigiClockState)
        tiltState = messenger.request(GetKaigiTiltState)
    }

    @Composable
    override fun Content() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            lastError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            KaigiClockPluginView(
                state = clockState,
                onShiftTo = { epochMillis -> runClockRequest { messenger.request(ShiftKaigiClockTo(epochMillis)) } },
                onReset = { runClockRequest { messenger.request(ResetKaigiClock) } },
                onRefresh = { runClockRequest { messenger.request(GetKaigiClockState) } },
            )
            HorizontalDivider()
            KaigiTiltPluginView(
                state = tiltState,
                onSet = { pitchDegrees, rollDegrees ->
                    runTiltRequest { messenger.request(SetKaigiTilt(pitchDegrees, rollDegrees)) }
                },
                onReset = { runTiltRequest { messenger.request(ResetKaigiTilt) } },
            )
        }
    }

    // The newest click decides each control, so an in-flight reply is abandoned rather than allowed
    // to land after it. The agent applied that earlier request all the same, so nothing is lost by
    // dropping the reply.
    private fun runClockRequest(block: suspend () -> KaigiClockState) {
        pendingClockRequest?.cancel()
        pendingClockRequest = pluginScope.launch {
            // Catch the messaging failure specifically — runCatching would also swallow the
            // CancellationException that cancels this coroutine.
            try {
                clockState = block()
                lastError = null
            } catch (e: JetWhaleMessagingException) {
                lastError = e.message ?: e::class.simpleName ?: "The request failed"
            }
        }
    }

    private fun runTiltRequest(block: suspend () -> KaigiTiltState) {
        pendingTiltRequest?.cancel()
        pendingTiltRequest = pluginScope.launch {
            try {
                tiltState = block()
                lastError = null
            } catch (e: JetWhaleMessagingException) {
                lastError = e.message ?: e::class.simpleName ?: "The request failed"
            }
        }
    }

    override val mcpCommands: List<JetWhaleMcpCommand> = listOf(
        object : JetWhaleMcpCommand() {
            override val name = "io.github.droidkaigi.confsched2026.clock.getState"
            override val description = "Returns the app's current time and how far it is shifted from the system clock."

            override suspend fun execute(arguments: JetWhaleMcpArguments): String =
                messenger.request(GetKaigiClockState).toJson()
        },
        object : JetWhaleMcpCommand() {
            override val name = "io.github.droidkaigi.confsched2026.clock.setNow"
            override val description =
                "Shifts the app's clock so that it reads the given instant and keeps ticking from there."

            private val instant by string("The instant the app should read, in ISO-8601 (e.g. 2026-09-02T10:00:00+09:00).")

            override suspend fun execute(arguments: JetWhaleMcpArguments): String {
                val text = arguments[instant]
                val target = Instant.parseOrNull(text)
                    ?: throw JetWhaleMcpArgumentException("'$text' is not an ISO-8601 instant.")
                return messenger.request(ShiftKaigiClockTo(target.toEpochMilliseconds())).toJson()
            }
        },
        object : JetWhaleMcpCommand() {
            override val name = "io.github.droidkaigi.confsched2026.clock.reset"
            override val description = "Returns the app's clock to the system time."

            override suspend fun execute(arguments: JetWhaleMcpArguments): String =
                messenger.request(ResetKaigiClock).toJson()
        },
        object : JetWhaleMcpCommand() {
            override val name = "io.github.droidkaigi.confsched2026.tilt.getState"
            override val description = "Returns whether the app's device tilt is pinned and, if so, at which angles."

            override suspend fun execute(arguments: JetWhaleMcpArguments): String =
                messenger.request(GetKaigiTiltState).toJson()
        },
        object : JetWhaleMcpCommand() {
            override val name = "io.github.droidkaigi.confsched2026.tilt.set"
            override val description =
                "Pins the app's device tilt at the given angles, in degrees, until a reset. Tilt-driven effects read the pinned value instead of the sensor."

            private val pitchDegrees by string("Rotation about the screen's horizontal axis, -90 to 90; positive tips the top edge toward the ground.")
            private val rollDegrees by string("Rotation about the screen's vertical axis, -180 to 180; positive tips the left edge toward the ground.")

            override suspend fun execute(arguments: JetWhaleMcpArguments): String {
                val pitch = arguments[pitchDegrees].toTiltDegrees("pitchDegrees", min = -90f, max = 90f)
                val roll = arguments[rollDegrees].toTiltDegrees("rollDegrees", min = -180f, max = 180f)
                return messenger.request(SetKaigiTilt(pitchDegrees = pitch, rollDegrees = roll)).toJson()
            }
        },
        object : JetWhaleMcpCommand() {
            override val name = "io.github.droidkaigi.confsched2026.tilt.reset"
            override val description = "Returns the app's device tilt to the platform sensor."

            override suspend fun execute(arguments: JetWhaleMcpArguments): String =
                messenger.request(ResetKaigiTilt).toJson()
        },
    )
}

private fun String.toTiltDegrees(name: String, min: Float, max: Float): Float {
    val degrees = toFloatOrNull()
        ?: throw JetWhaleMcpArgumentException("'$this' is not a number for $name.")
    if (degrees < min || degrees > max) {
        throw JetWhaleMcpArgumentException("$name must be between $min and $max, got $degrees.")
    }
    return degrees
}

private fun KaigiClockState.toJson(): String = buildJsonObject {
    put("now", Instant.fromEpochMilliseconds(nowEpochMillis).toString())
    put("offsetMillis", offsetMillis)
}.toString()

private fun KaigiTiltState.toJson(): String = buildJsonObject {
    put("overridden", overridden)
    if (overridden) {
        put("pitchDegrees", pitchDegrees)
        put("rollDegrees", rollDegrees)
    }
}.toString()
