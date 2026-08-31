package io.github.droidkaigi.confsched.feature.doodle

import androidx.navigation3.runtime.NavKey
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import kotlinx.serialization.Serializable

@Serializable
data class DoodleNavKey(val target: DoodleTarget) : NavKey
