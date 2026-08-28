package io.github.droidkaigi.confsched.core.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.view.Surface
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.emptyFlow

@Composable
internal actual fun rememberDeviceTilts(): Flow<DeviceTilt> {
    val context = LocalContext.current
    return remember(context, context::rotationVectorTilts)
}

private fun Context.rotationVectorTilts(): Flow<DeviceTilt> {
    val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        ?: return emptyFlow()
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    return callbackFlow {
        val rotationMatrix = FloatArray(MATRIX_SIZE)
        val screenMatrix = FloatArray(MATRIX_SIZE)
        val orientation = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                val (axisX, axisY) = screenAxes(windowManager.displayRotation)
                SensorManager.remapCoordinateSystem(rotationMatrix, axisX, axisY, screenMatrix)
                SensorManager.getOrientation(screenMatrix, orientation)
                trySend(
                    DeviceTilt(
                        pitchDegrees = orientation[1].toDegrees(),
                        rollDegrees = -orientation[2].toDegrees(),
                    ),
                )
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
        }

        // The main looper delivers every reading, so the Compose state behind it is written on the
        // thread that composition and drawing read it from.
        sensorManager.registerListener(
            listener,
            rotationVector,
            SensorManager.SENSOR_DELAY_GAME,
            Handler(Looper.getMainLooper()),
        )
        awaitClose { sensorManager.unregisterListener(listener) }
    }.conflate()
}

/** The sensor axes that map onto the screen's own, so a rotated display does not swap pitch and roll. */
private fun screenAxes(displayRotation: Int): Pair<Int, Int> = when (displayRotation) {
    Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
    Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
    Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
    else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
}

// Context.getDisplay() needs API 30 and a visual context; the deprecated path covers every version
// the app supports.
@Suppress("DEPRECATION")
private val WindowManager.displayRotation: Int
    get() = defaultDisplay.rotation

private fun Float.toDegrees(): Float = (this * 180f / Math.PI).toFloat()

private const val MATRIX_SIZE = 9
