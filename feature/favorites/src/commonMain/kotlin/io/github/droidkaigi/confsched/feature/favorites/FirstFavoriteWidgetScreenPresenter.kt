package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.TargetPlatform
import io.github.droidkaigi.confsched.core.common.currentPlatform
import io.github.droidkaigi.confsched.core.model.Mascot

@Composable
context(_: FirstFavoriteWidgetPresenterContext)
fun firstFavoriteWidgetScreenPresenter(canPinWidget: Boolean, mascot: Mascot): FirstFavoriteWidgetScreenUiState =
    FirstFavoriteWidgetScreenUiState(
        pinSupport = when {
            canPinWidget -> FirstFavoriteWidgetPinSupport.Requestable
            currentPlatform == TargetPlatform.Ios -> FirstFavoriteWidgetPinSupport.ManualOnly
            else -> FirstFavoriteWidgetPinSupport.Unsupported
        },
        mascot = mascot,
    )
