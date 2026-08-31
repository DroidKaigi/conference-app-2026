package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.runtime.Composable
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.ui.rememberFavoritesWidgetPinner

@Composable
context(screenContext: FirstFavoriteWidgetScreenContext)
fun FirstFavoriteWidgetScreenRoot(
    mascot: Mascot,
    onNavigateBack: () -> Unit,
) {
    val pinWidget = rememberFavoritesWidgetPinner()

    val uiState = context(screenContext.presenterContext) {
        firstFavoriteWidgetScreenPresenter(canAddWidget = pinWidget != null, mascot = mascot)
    }
    FirstFavoriteWidgetScreen(
        uiState = uiState,
        onAddWidgetClick = {
            pinWidget?.invoke()
            onNavigateBack()
        },
        onLaterClick = onNavigateBack,
    )
}
