package io.github.droidkaigi.confsched.jetwhale.protocol

import com.kitakkun.jetwhale.protocol.messaging.JetWhaleRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** [pitchDegrees] and [rollDegrees] are meaningful only while [overridden] is true. */
@SerialName("kaigitilt/state")
@Serializable
data class KaigiTiltState(
    val overridden: Boolean,
    val pitchDegrees: Float,
    val rollDegrees: Float,
)

@SerialName("kaigitilt/get_state")
@Serializable
data object GetKaigiTiltState : JetWhaleRequest<KaigiTiltState>

/** Pins the app's device tilt at the given angles until [ResetKaigiTilt]. */
@SerialName("kaigitilt/set")
@Serializable
data class SetKaigiTilt(
    val pitchDegrees: Float,
    val rollDegrees: Float,
) : JetWhaleRequest<KaigiTiltState>

@SerialName("kaigitilt/reset")
@Serializable
data object ResetKaigiTilt : JetWhaleRequest<KaigiTiltState>
