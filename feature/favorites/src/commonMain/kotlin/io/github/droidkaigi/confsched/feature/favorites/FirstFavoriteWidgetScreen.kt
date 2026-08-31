package io.github.droidkaigi.confsched.feature.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.feature.favorites.component.FirstFavoriteDialogCard
import io.github.droidkaigi.confsched.feature.favorites.component.FirstFavoriteDialogDefaults
import io.github.droidkaigi.confsched.feature.favorites.component.FirstFavoriteMascotImage
import io.github.droidkaigi.confsched.feature.favorites.component.FirstFavoriteWidgetPreviewCard
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.Res
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_not_now
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_widget_add
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_widget_description
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_widget_eyebrow
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_widget_manual
import io.github.droidkaigi.confsched.feature.favorites.generated.resources.first_favorite_widget_title
import org.jetbrains.compose.resources.stringResource

/** The character that joins the one the first step showed, so a second friend pops in. */
private val Mascot.friend: Mascot get() = Mascot.entries[(ordinal + 1) % Mascot.entries.size]

private const val FIRST_FAVORITE_WIDGET_DIALOG_SEED = 888
private const val FIRST_FAVORITE_WIDGET_BUTTON_SEED = 779

@Composable
fun FirstFavoriteWidgetScreen(
    uiState: FirstFavoriteWidgetScreenUiState,
    onAddWidgetClick: () -> Unit,
    onLaterClick: () -> Unit,
) {
    FirstFavoriteDialogCard(seed = FIRST_FAVORITE_WIDGET_DIALOG_SEED) {
        Text(
            text = stringResource(Res.string.first_favorite_widget_eyebrow),
            style = FirstFavoriteDialogDefaults.eyebrowStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(Res.string.first_favorite_widget_title),
            style = FirstFavoriteDialogDefaults.titleStyle,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        // The mascot peeks past the widget's corner, so the stage keeps room for it below.
        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
            FirstFavoriteWidgetPreviewCard()
            FirstFavoriteMascotImage(
                mascot = uiState.mascot.friend,
                height = 58.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 14.dp, y = 32.dp)
                    .rotate(8f),
            )
        }
        Text(
            text = stringResource(Res.string.first_favorite_widget_description),
            style = FirstFavoriteDialogDefaults.descriptionStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (uiState.canAddWidget) {
            KaigiButton(
                onClick = onAddWidgetClick,
                seed = FIRST_FAVORITE_WIDGET_BUTTON_SEED,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(Res.string.first_favorite_widget_add), style = KaigiButtonDefaults.labelStyle)
            }
        } else {
            Text(
                text = stringResource(Res.string.first_favorite_widget_manual),
                style = FirstFavoriteDialogDefaults.descriptionStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        TextButton(onClick = onLaterClick) {
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
private fun FirstFavoriteWidgetScreenPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FirstFavoriteWidgetScreen(
            uiState = FirstFavoriteWidgetScreenUiState(canAddWidget = true, mascot = Mascot.E),
            onAddWidgetClick = {},
            onLaterClick = {},
        )
    }
}
