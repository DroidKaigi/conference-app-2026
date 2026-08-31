package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.feature.favorites.component.FirstFavoriteDialogCard
import io.github.droidkaigi.confsched.feature.favorites.component.FirstFavoriteDialogDefaults
import io.github.droidkaigi.confsched.feature.favorites.component.FirstFavoriteHeroView
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.Res
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_description
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_eyebrow
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_not_now
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_title
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_turn_on_notifications
import org.jetbrains.compose.resources.stringResource

private const val FIRST_FAVORITE_DIALOG_SEED = 777
private const val FIRST_FAVORITE_PRIMARY_BUTTON_SEED = 778

@Composable
fun FirstFavoriteNotificationScreen(
    uiState: FirstFavoriteNotificationScreenUiState,
    onTurnOnNotificationsClick: () -> Unit,
    onLaterClick: () -> Unit,
) {
    FirstFavoriteDialogCard(seed = FIRST_FAVORITE_DIALOG_SEED) {
        FirstFavoriteHeroView()
        Text(
            text = stringResource(Res.string.first_favorite_eyebrow),
            style = FirstFavoriteDialogDefaults.eyebrowStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(Res.string.first_favorite_title),
            style = FirstFavoriteDialogDefaults.titleStyle,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.first_favorite_description),
            style = FirstFavoriteDialogDefaults.descriptionStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        KaigiButton(
            onClick = onTurnOnNotificationsClick,
            seed = FIRST_FAVORITE_PRIMARY_BUTTON_SEED,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isAnswering,
        ) {
            Text(
                text = stringResource(Res.string.first_favorite_turn_on_notifications),
                style = KaigiButtonDefaults.labelStyle,
            )
        }
        TextButton(onClick = onLaterClick, enabled = !uiState.isAnswering) {
            Text(
                text = stringResource(Res.string.first_favorite_not_now),
                style = FirstFavoriteDialogDefaults.descriptionStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@LocalePreviews
@Composable
private fun FirstFavoriteNotificationScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FirstFavoriteNotificationScreen(
            uiState = FirstFavoriteNotificationScreenUiState(isAnswering = false),
            onTurnOnNotificationsClick = {},
            onLaterClick = {},
        )
    }
}
