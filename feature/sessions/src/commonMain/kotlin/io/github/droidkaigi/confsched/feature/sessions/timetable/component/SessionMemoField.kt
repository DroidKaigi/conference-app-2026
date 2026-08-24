package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchDefaults
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.memo
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.memo_placeholder
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SessionMemoField(
    memo: String,
    seed: Int,
    onMemoChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Seeded once: every edit is written straight through, so a later emission carries the draft
    // back and re-keying on it would rewind whatever was typed in the meantime.
    var draft by remember { mutableStateOf(memo) }
    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(seed),
        roughness = SketchDefaults.roughness,
        tremor = SketchDefaults.tremor,
        cornerRadius = SessionMemoFieldDefaults.cornerRadius,
        borderThickness = SessionMemoFieldDefaults.borderThickness,
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SessionSectionLabel(text = stringResource(Res.string.memo))
        Box {
            BasicTextField(
                value = draft,
                onValueChange = { text ->
                    draft = text
                    onMemoChange(text)
                },
                textStyle = LocalTextStyle.current.merge(MaterialTheme.typography.bodyMedium)
                    .copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = SessionMemoFieldDefaults.minHeight)
                    .padding(16.dp),
            ) { innerTextField ->
                if (draft.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.memo_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                innerTextField()
            }
            Box(Modifier.matchParentSize().sketchBorder(shape, MaterialTheme.colorScheme.outline))
        }
    }
}

private object SessionMemoFieldDefaults {
    val cornerRadius = 20.dp
    val borderThickness = 2.dp
    val minHeight = 76.dp
}

@LocalePreviews
@Composable
private fun SessionMemoFieldPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SessionMemoField(memo = "", seed = 640, onMemoChange = {}, modifier = Modifier.padding(24.dp))
    }
}
