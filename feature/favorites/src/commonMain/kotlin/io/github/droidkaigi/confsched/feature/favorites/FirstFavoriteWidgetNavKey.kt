package io.github.droidkaigi.confsched.feature.favorites

import androidx.navigation3.runtime.NavKey
import io.github.droidkaigi.confsched.core.model.Mascot
import kotlinx.serialization.Serializable

@Serializable
data class FirstFavoriteWidgetNavKey(val mascot: Mascot) : NavKey
