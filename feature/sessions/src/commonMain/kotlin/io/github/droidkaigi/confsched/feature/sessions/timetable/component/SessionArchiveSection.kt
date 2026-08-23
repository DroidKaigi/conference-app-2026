package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Description
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.PlayCircle
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.TimetableItemAsset
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchDefaults
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.archive
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.archive_slide
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.archive_video
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SessionArchiveSection(
    asset: TimetableItemAsset,
    seed: Int,
    onVideoClick: (String) -> Unit,
    onSlideClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(seed),
        cornerRadius = SessionArchiveSectionDefaults.cornerRadius,
        borderThickness = SessionArchiveSectionDefaults.borderThickness,
        roughness = SketchDefaults.roughness,
        tremor = SketchDefaults.tremor,
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SessionSectionLabel(text = stringResource(Res.string.archive))
        Box {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                asset.videoUrl?.let { url ->
                    ArchiveEntryRow(
                        imageVector = KaigiIcons.Default.PlayCircle,
                        label = stringResource(Res.string.archive_video),
                        onClick = { onVideoClick(url) },
                    )
                }
                if (asset.videoUrl != null && asset.slideUrl != null) {
                    SketchHorizontalDivider(seed = seed + 1, thickness = SessionArchiveSectionDefaults.dividerThickness)
                }
                asset.slideUrl?.let { url ->
                    ArchiveEntryRow(
                        imageVector = KaigiIcons.Default.Description,
                        label = stringResource(Res.string.archive_slide),
                        onClick = { onSlideClick(url) },
                    )
                }
            }
            Box(Modifier.matchParentSize().sketchBorder(shape, MaterialTheme.colorScheme.outline))
        }
    }
}

@Composable
private fun ArchiveEntryRow(
    imageVector: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(SessionArchiveSectionDefaults.iconSize),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

private object SessionArchiveSectionDefaults {
    val cornerRadius = 12.dp
    val borderThickness = 2.dp
    val dividerThickness = 1.dp
    val iconSize = 20.dp
}

@LocalePreviews
@Composable
private fun SessionArchiveSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SessionArchiveSection(
            asset = TimetableItemAsset(
                videoUrl = "https://example.com/sessions/d1b/video",
                slideUrl = "https://example.com/sessions/d1b/slides",
            ),
            seed = 620,
            onVideoClick = {},
            onSlideClick = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}
