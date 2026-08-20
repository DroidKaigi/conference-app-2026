package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.CalendarAddOn
import io.github.droidkaigi.confsched.core.designsystem.icon.FavoriteBorder
import io.github.droidkaigi.confsched.core.designsystem.icon.FavoriteFilled
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Share
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.add_favorite
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.add_to_calendar
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.remove_favorite
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.share
import org.jetbrains.compose.resources.stringResource

/** A cancelled session drops the calendar action, since there is no longer an event to add. */
@Composable
internal fun SessionDetailToolbar(
    isFavorite: Boolean,
    isCancelled: Boolean,
    onCalendarClick: () -> Unit,
    onShareClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SketchSurface(seed = SessionDetailToolbarDefaults.TOOLBAR_SEED) {
            Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isCancelled) {
                    ToolbarAction(
                        imageVector = KaigiIcons.Default.CalendarAddOn,
                        contentDescription = stringResource(Res.string.add_to_calendar),
                        onClick = onCalendarClick,
                    )
                }
                ToolbarAction(
                    imageVector = KaigiIcons.Default.Share,
                    contentDescription = stringResource(Res.string.share),
                    onClick = onShareClick,
                )
            }
        }
        SketchSurface(seed = SessionDetailToolbarDefaults.FAB_SEED) {
            ToolbarAction(
                imageVector = if (isFavorite) KaigiIcons.Default.FavoriteFilled else KaigiIcons.Default.FavoriteBorder,
                contentDescription = stringResource(if (isFavorite) Res.string.remove_favorite else Res.string.add_favorite),
                onClick = onBookmarkClick,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
private fun SketchSurface(seed: Int, content: @Composable () -> Unit) {
    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(seed),
        cornerRadius = SessionDetailToolbarDefaults.cornerRadius,
        borderThickness = SessionDetailToolbarDefaults.borderThickness,
    )
    Box {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
        )
        content()
        Box(Modifier.matchParentSize().sketchBorder(shape, MaterialTheme.colorScheme.outline))
    }
}

@Composable
private fun ToolbarAction(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(SessionDetailToolbarDefaults.actionSize)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(SessionDetailToolbarDefaults.iconSize),
        )
    }
}

private object SessionDetailToolbarDefaults {
    const val TOOLBAR_SEED = 932
    const val FAB_SEED = 933

    val cornerRadius = 20.dp
    val borderThickness = 2.dp
    val actionSize = 40.dp
    val iconSize = 24.dp
}

@LocalePreviews
@Composable
private fun SessionDetailToolbarPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SessionDetailToolbar(
            isFavorite = true,
            isCancelled = false,
            onCalendarClick = {},
            onShareClick = {},
            onBookmarkClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
