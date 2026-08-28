package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.description
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.show_less
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.show_more
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SessionDescriptionSection(
    description: String,
    isExpanded: Boolean,
    seed: Int,
    onExpansionToggleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isTruncated by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SessionSectionLabel(text = stringResource(Res.string.description))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = if (isExpanded) Int.MAX_VALUE else SessionDescriptionSectionDefaults.COLLAPSED_LINES,
            overflow = TextOverflow.Ellipsis,
            // Latched: expanding removes the overflow, and without the latch the control that
            // collapses the text again would go with it.
            onTextLayout = { result -> isTruncated = isTruncated || result.hasVisualOverflow },
        )
        if (isTruncated) {
            KaigiButton(onClick = onExpansionToggleClick, seed = seed, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(if (isExpanded) Res.string.show_less else Res.string.show_more),
                    style = KaigiButtonDefaults.labelStyle,
                )
            }
        }
    }
}

private object SessionDescriptionSectionDefaults {
    const val COLLAPSED_LINES = 2
}

@LocalePreviews
@Composable
private fun SessionDescriptionSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SessionDescriptionSection(
            description = "本セッションでは、サンプルアプリの設計とその変遷をたどります。折り返しと「もっとみる」の挙動を確かめられるだけの長さを持たせたプレースホルダーの本文です。",
            isExpanded = false,
            seed = 630,
            onExpansionToggleClick = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}
