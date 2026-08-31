package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
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
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.cancelled_session
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SessionCancelledBanner(contentInsets: PaddingValues, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(Res.string.cancelled_session),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(contentInsets)
            .padding(horizontal = 24.dp, vertical = 10.dp),
    )
}

@LocalePreviews
@Composable
private fun SessionCancelledBannerPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SessionCancelledBanner(contentInsets = PaddingValues())
    }
}
