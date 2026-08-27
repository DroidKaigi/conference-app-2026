package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            // The box owns the height the design gives it, rather than inheriting whatever
            // single line the text field's own font metrics come to.
            .height(KaigiTextFieldDefaults.height)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(KaigiTextFieldDefaults.contentPadding),
            textStyle = KaigiTextFieldDefaults.textStyle
                .copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            keyboardOptions = keyboardOptions,
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        )
        Box(Modifier.matchParentSize().sketchBorder(shape, borderColor))
    }
}

object KaigiTextFieldDefaults {
    val cornerRadius: Dp = 12.dp
    val borderThickness: Dp = 1.5.dp
    val height: Dp = 44.dp
    val contentPadding = PaddingValues(horizontal = 12.dp)

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
                value = "Speaker A",
                onValueChange = {},
                seed = 881,
                keyboardOptions = KeyboardOptions.Default,
            )
            KaigiTextField(
                value = "https://example.com/user",
                onValueChange = {},
                seed = 882,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            )
            KaigiTextField(
                value = "",
                onValueChange = {},
                seed = 883,
                keyboardOptions = KeyboardOptions.Default,
                isError = true,
            )
        }
    }
}
