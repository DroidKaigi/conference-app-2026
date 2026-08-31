package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.runtime.Composable

@Composable
context(_: FirstFavoriteWidgetPresenterContext)
fun firstFavoriteWidgetScreenPresenter(canAddWidget: Boolean): FirstFavoriteWidgetScreenUiState =
    FirstFavoriteWidgetScreenUiState(canAddWidget = canAddWidget)
