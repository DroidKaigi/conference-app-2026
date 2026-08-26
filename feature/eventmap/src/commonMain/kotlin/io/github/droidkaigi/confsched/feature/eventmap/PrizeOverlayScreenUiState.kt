package io.github.droidkaigi.confsched.feature.eventmap

import io.github.droidkaigi.confsched.core.model.Prize
import kotlinx.collections.immutable.PersistentList

data class PrizeOverlayScreenUiState(
    val prizes: PersistentList<Prize>,
    val initialPage: Int,
)
