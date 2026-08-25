package io.github.droidkaigi.confsched.feature.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

@Composable
internal fun SettingsGroupSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = title,
            style = groupTitleStyle,
            color = MaterialTheme.colorScheme.onSurface,
        )
        content()
    }
}

/**
 * The heading a group carries: the title/medium size in the display face.
 *
 * The face is read back off a headline role, so it follows the font preference, which decides
 * per role rather than per family.
 */
private val groupTitleStyle: TextStyle
    @Composable get() = MaterialTheme.typography.titleMedium.copy(
        fontFamily = MaterialTheme.typography.headlineSmall.fontFamily,
        fontWeight = FontWeight.Bold,
    )

@Preview
@Composable
private fun SettingsGroupSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SettingsGroupSection(title = "Font", modifier = Modifier.padding(16.dp)) {
            Text("Option")
        }
    }
}
