package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.model.Mascot

@Composable
context(_: FirstFavoriteWidgetPresenterContext)
fun firstFavoriteWidgetScreenPresenter(canAddWidget: Boolean, mascot: Mascot): FirstFavoriteWidgetScreenUiState =
    FirstFavoriteWidgetScreenUiState(canAddWidget = canAddWidget, mascot = mascot)
