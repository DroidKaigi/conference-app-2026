package io.github.droidkaigi.confsched.feature.about.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.designsystem.icon.ChevronRight
import io.github.droidkaigi.confsched.core.designsystem.icon.Groups
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

@Composable
internal fun AboutNavigationRow(
    label: String,
    leadingIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supporting: String? = null,
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        leadingContent = { Icon(imageVector = leadingIcon, contentDescription = null) },
        headlineContent = { Text(label) },
        supportingContent = supporting?.let { text -> { Text(text) } },
        trailingContent = {
            Icon(imageVector = KaigiIcons.Default.ChevronRight, contentDescription = null)
        },
    )
}

@LocalePreviews
@Composable
private fun AboutNavigationRowPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutNavigationRow(
            label = "Contributors",
            leadingIcon = KaigiIcons.Default.Groups,
            onClick = {},
            supporting = "Optional supporting text",
        )
    }
}
