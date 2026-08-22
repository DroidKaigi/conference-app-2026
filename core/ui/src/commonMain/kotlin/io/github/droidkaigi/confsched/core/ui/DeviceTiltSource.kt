package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * The device tilt, as a holder a consumer reads during the draw phase.
 *
 * Read the state inside `graphicsLayer` rather than in composition: the sensor ticks at tens of
 * hertz, and a read in composition recomposes the tree at that rate.
 *
 * The tilt is reported as measured, so an effect that damps or drops its motion when the platform
 * asks for reduced motion decides that for itself.
 */
@Stable
interface DeviceTiltSource {
    /** The current tilt, updated while the caller is composed and the app is on screen. */
    @Composable
    fun tiltAsState(): State<DeviceTilt>
}

/** Reports [DeviceTilt.Level] and reads no sensor. */
object LevelDeviceTiltSource : DeviceTiltSource {
    private val level = object : State<DeviceTilt> {
        override val value: DeviceTilt = DeviceTilt.Level
    }

    @Composable
    override fun tiltAsState(): State<DeviceTilt> = level
}

/**
 * The source a composable reads the tilt from.
 *
 * A preview and a screenshot test read [LevelDeviceTiltSource] while the app reads the platform
 * sensor, so a golden does not vary with how the recording machine was held. The default is a
 * working value rather than an error for that reason: a composition with no provider is the pinned
 * one.
 */
val LocalDeviceTiltSource = staticCompositionLocalOf<DeviceTiltSource> { LevelDeviceTiltSource }

/** The platform sensor as a source, living as long as the composition that remembers it. */
@Composable
fun rememberDeviceTiltSource(): DeviceTiltSource {
    val tilts = rememberDeviceTilts()
    val coroutineScope = rememberCoroutineScope()
    return remember(tilts, coroutineScope) { SubscribedDeviceTiltSource(tilts, coroutineScope) }
}

@Composable
internal expect fun rememberDeviceTilts(): Flow<DeviceTilt>

private class SubscribedDeviceTiltSource(
    tilts: Flow<DeviceTilt>,
    coroutineScope: CoroutineScope,
) : DeviceTiltSource {
    // No stop timeout: outliving the last consumer is the one thing the sharing policy prevents.
    private val tilt = tilts.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), DeviceTilt.Level)

    @Composable
    override fun tiltAsState(): State<DeviceTilt> = tilt.collectAsStateWithLifecycle()
}
