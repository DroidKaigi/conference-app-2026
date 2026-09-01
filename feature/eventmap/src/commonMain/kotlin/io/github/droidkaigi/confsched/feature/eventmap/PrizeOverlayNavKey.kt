package io.github.droidkaigi.confsched.feature.eventmap

import io.github.droidkaigi.confsched.core.common.OverlayNavKey
import kotlinx.serialization.Serializable

@Serializable
data class PrizeOverlayNavKey(val page: Int) : OverlayNavKey
