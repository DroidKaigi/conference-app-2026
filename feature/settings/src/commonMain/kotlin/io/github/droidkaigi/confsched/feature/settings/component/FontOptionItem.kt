package io.github.droidkaigi.confsched.feature.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchDefaults
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.sketchBorder

@Composable
internal fun FontOptionItem(
    label: String,
    // The option sets its name in the face it selects, so the style comes from the caller
    // rather than from the typography the preference in force installs.
    labelStyle: TextStyle,
    selected: Boolean,
    seed: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(seed),
        roughness = SketchDefaults.roughness,
        tremor = SketchDefaults.tremor,
        cornerRadius = FontOptionDefaults.cornerRadius,
        borderThickness = FontOptionDefaults.borderThickness,
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FontOptionDefaults.height)
            .clip(shape)
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .sketchBorder(shape = shape, color = MaterialTheme.colorScheme.outline)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(FontOptionDefaults.checkSize)) {
            if (selected) {
                SelectionCheckIcon(modifier = Modifier.size(FontOptionDefaults.checkSize))
            }
        }
        Text(
            text = label,
            style = labelStyle,
            color = if (selected) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
    }
}

private object FontOptionDefaults {
    val height = 64.dp
    val cornerRadius = 12.dp
    val borderThickness = 2.dp
    val checkSize = 20.dp
}

@Preview
@Composable
private fun FontOptionItemPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FontOptionItem(
                label = "Default",
                labelStyle = MaterialTheme.typography.titleLarge,
                selected = true,
                seed = 1,
                onClick = {},
            )
            FontOptionItem(
                label = "Noto Sans",
                labelStyle = MaterialTheme.typography.titleLarge,
                selected = false,
                seed = 2,
                onClick = {},
            )
        }
    }
}
