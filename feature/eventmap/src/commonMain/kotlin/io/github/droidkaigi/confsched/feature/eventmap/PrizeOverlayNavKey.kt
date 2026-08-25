package io.github.droidkaigi.confsched.feature.eventmap

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class PrizeOverlayNavKey(val page: Int) : NavKey
