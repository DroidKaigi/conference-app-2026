package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.target_audience
import org.jetbrains.compose.resources.stringResource

// A speaker writes the list, and most of them mark the lines themselves; a bullet added here
// lands beside the one already in the text.
@Composable
internal fun SessionTargetAudienceSection(
    targetAudience: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SessionSectionLabel(text = stringResource(Res.string.target_audience))
        for (line in targetAudience.lines().filter { it.isNotBlank() }) {
            Text(
                text = line,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@LocalePreviews
@Composable
private fun SessionTargetAudienceSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SessionTargetAudienceSection(
            targetAudience = "- モダンなAndroidアプリ開発の設計に興味がある方\n- Kotlin Multiplatformの実践的な適用例を知りたい方",
            modifier = Modifier.padding(24.dp),
        )
    }
}
