package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.emptyFlow
import platform.CoreMotion.CMAttitude
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIInterfaceOrientation
import platform.UIKit.UIInterfaceOrientationLandscapeLeft
import platform.UIKit.UIInterfaceOrientationLandscapeRight
import platform.UIKit.UIInterfaceOrientationPortrait
import platform.UIKit.UIInterfaceOrientationPortraitUpsideDown
import platform.UIKit.UIWindowScene
import kotlin.math.PI

// CoreMotion hands out one hardware stream, so a second manager would stop the first one's updates.
private val motionManager = CMMotionManager()

@Composable
internal actual fun rememberDeviceTilts(): Flow<DeviceTilt> = remember(::deviceMotionTilts)

private fun deviceMotionTilts(): Flow<DeviceTilt> {
    if (!motionManager.deviceMotionAvailable) return emptyFlow()

    return callbackFlow {
        motionManager.deviceMotionUpdateInterval = UPDATE_INTERVAL_SECONDS
        // The main queue delivers every reading, so the Compose state behind it is written on the
        // thread that composition and drawing read it from.
        motionManager.startDeviceMotionUpdatesToQueue(NSOperationQueue.mainQueue) { motion, _ ->
            val attitude = motion?.attitude ?: return@startDeviceMotionUpdatesToQueue
            trySend(attitude.toDeviceTilt(interfaceOrientation()))
        }
        awaitClose(motionManager::stopDeviceMotionUpdates)
    }.conflate()
}

/**
 * Turns the portrait-referenced attitude into the screen's own axes, so a rotated interface does not
 * swap pitch and roll.
 *
 * Swapping the two angles is exact in portrait; in landscape it holds for a tilt about one axis and
 * carries the ±180° angle in [DeviceTilt.pitchDegrees], which the documented ranges give to roll.
 * Rotating the attitude itself and re-deriving both angles, as the Android source does, would close
 * that gap.
 */
private fun CMAttitude.toDeviceTilt(orientation: UIInterfaceOrientation): DeviceTilt {
    // CoreMotion measures both angles in the direction opposite to the one DeviceTilt documents.
    val screenPitch = -pitch.toDegrees()
    val screenRoll = -roll.toDegrees()

    return when (orientation) {
        UIInterfaceOrientationLandscapeLeft -> DeviceTilt(
            pitchDegrees = screenRoll,
            rollDegrees = -screenPitch,
        )

        UIInterfaceOrientationLandscapeRight -> DeviceTilt(
            pitchDegrees = -screenRoll,
            rollDegrees = screenPitch,
        )

        UIInterfaceOrientationPortraitUpsideDown -> DeviceTilt(
            pitchDegrees = -screenPitch,
            rollDegrees = -screenRoll,
        )

        else -> DeviceTilt(
            pitchDegrees = screenPitch,
            rollDegrees = screenRoll,
        )
    }
}

private fun interfaceOrientation(): UIInterfaceOrientation =
    UIApplication.sharedApplication.connectedScenes
        .filterIsInstance<UIWindowScene>()
        .firstOrNull()
        ?.interfaceOrientation
        ?: UIInterfaceOrientationPortrait

private fun Double.toDegrees(): Float = (this * 180.0 / PI).toFloat()

private const val UPDATE_INTERVAL_SECONDS = 1.0 / 60.0
