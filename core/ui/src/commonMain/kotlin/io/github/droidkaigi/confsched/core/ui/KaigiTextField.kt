package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/**
 * A single line of entry in a hand-sketched filled box.
 *
 * @param value the text the field shows.
 * @param onValueChange called with the text after every edit.
 * @param placeholder the text shown while [value] is empty.
 * @param seed the value the box is drawn from. The same seed always produces the same box,
 *   so give neighbouring fields different ones or a column of them reads as a repeat.
 * @param keyboardOptions the software keyboard the field asks for.
 * @param modifier the [Modifier] applied to the field.
 * @param isError whether the outline takes the error colour.
 */
@Composable
fun KaigiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    seed: Int,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(seed),
        roughness = SketchDefaults.roughness,
        tremor = SketchDefaults.tremor,
        cornerRadius = KaigiTextFieldDefaults.cornerRadius,
        borderThickness = KaigiTextFieldDefaults.borderThickness,
    )
    val borderColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.outline
    }
    Box(modifier = modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(KaigiTextFieldDefaults.contentPadding),
            textStyle = KaigiTextFieldDefaults.textStyle
                .copy(color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = keyboardOptions,
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        ) { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = KaigiTextFieldDefaults.textStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            innerTextField()
        }
        Box(Modifier.matchParentSize().sketchBorder(shape, borderColor))
    }
}

object KaigiTextFieldDefaults {
    val cornerRadius: Dp = 12.dp
    val borderThickness: Dp = 1.5.dp
    val contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)

    val textStyle
        @Composable get() = MaterialTheme.typography.bodyMedium
}

@Preview
@Composable
private fun KaigiTextFieldPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KaigiTextField(
                value = "droidkaigi",
                onValueChange = {},
                placeholder = "",
                seed = 881,
                keyboardOptions = KeyboardOptions.Default,
            )
            KaigiTextField(
                value = "",
                onValueChange = {},
                placeholder = "https://example.com/user",
                seed = 882,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            )
            KaigiTextField(
                value = "",
                onValueChange = {},
                placeholder = "",
                seed = 883,
                keyboardOptions = KeyboardOptions.Default,
                isError = true,
            )
        }
    }
}
