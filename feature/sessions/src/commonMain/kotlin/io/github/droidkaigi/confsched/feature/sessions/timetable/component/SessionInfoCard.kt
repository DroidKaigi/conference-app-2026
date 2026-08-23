package io.github.droidkaigi.confsched.feature.sessions.timetable.component

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
import io.github.droidkaigi.confsched.core.designsystem.icon.Category
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Language
import io.github.droidkaigi.confsched.core.designsystem.icon.LocationOn
import io.github.droidkaigi.confsched.core.designsystem.icon.Schedule
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchDefaults
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.current
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.language_english
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.language_japanese
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.language_mixed
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.language_with_interpretation
import kotlinx.datetime.number
import org.jetbrains.compose.resources.stringResource
import io.github.droidkaigi.confsched.core.model.Language as SessionLanguage

@Composable
internal fun SessionInfoCard(
    day: DroidKaigi2026Day,
    startsAt: String,
    endsAt: String,
    room: Room,
    language: SessionLanguage,
    hasInterpretation: Boolean,
    category: String?,
    seed: Int,
    modifier: Modifier = Modifier,
) {
    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(seed),
        roughness = SketchDefaults.roughness,
        tremor = SketchDefaults.tremor,
        cornerRadius = SessionInfoCardDefaults.cornerRadius,
        borderThickness = SessionInfoCardDefaults.borderThickness,
    )
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            InfoRow(
                imageVector = KaigiIcons.Default.Schedule,
                text = scheduleText(day = day, startsAt = startsAt, endsAt = endsAt),
            )
            InfoRow(imageVector = KaigiIcons.Default.LocationOn, text = room.locationText())
            InfoRow(
                imageVector = KaigiIcons.Default.Language,
                text = languageText(language = language, hasInterpretation = hasInterpretation),
            )
            if (category != null) {
                InfoRow(imageVector = KaigiIcons.Default.Category, text = category)
            }
        }
        Box(Modifier.matchParentSize().sketchBorder(shape, MaterialTheme.colorScheme.outline))
    }
}

@Composable
private fun InfoRow(imageVector: ImageVector, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(SessionInfoCardDefaults.iconSize),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun Room.locationText(): String = floor?.let { "$name (${it.label})" } ?: name

private fun scheduleText(day: DroidKaigi2026Day, startsAt: String, endsAt: String): String {
    val date = day.date
    val month = date.month.number.toString().padStart(2, '0')
    val dayOfMonth = date.day.toString().padStart(2, '0')
    return "${date.year}.$month.$dayOfMonth / $startsAt - $endsAt"
}

@Composable
private fun languageText(language: SessionLanguage, hasInterpretation: Boolean): String {
    val spoken = stringResource(language.labelResource())
    val interpreted = language.interpretedInto()
    return if (hasInterpretation && interpreted != null) {
        stringResource(Res.string.language_with_interpretation, spoken, stringResource(interpreted.labelResource()))
    } else {
        spoken
    }
}

private fun SessionLanguage.labelResource() = when (this) {
    SessionLanguage.JAPANESE -> Res.string.language_japanese
    SessionLanguage.ENGLISH -> Res.string.language_english
    SessionLanguage.MIXED -> Res.string.language_mixed
}

private object SessionInfoCardDefaults {
    val cornerRadius = 20.dp
    val borderThickness = 2.dp
    val iconSize = 20.dp
}

@LocalePreviews
@Composable
private fun SessionInfoCardPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        val item = TimetableItem.fake()
        SessionInfoCard(
            day = item.day,
            startsAt = item.startsAt,
            endsAt = item.endsAt,
            room = item.room,
            language = item.language,
            hasInterpretation = item.hasInterpretation,
            category = item.category?.current(),
            seed = 620,
            modifier = Modifier.padding(24.dp),
        )
    }
}
