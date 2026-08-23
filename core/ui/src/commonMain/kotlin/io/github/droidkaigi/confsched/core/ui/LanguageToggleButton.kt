package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.LanguageToggleEnglish
import io.github.droidkaigi.confsched.core.designsystem.icon.LanguageToggleJapanese
import io.github.droidkaigi.confsched.core.model.DisplayLanguage
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.show_in_english
import io.github.droidkaigi.confsched.core.ui.generated.resources.show_in_japanese
import org.jetbrains.compose.resources.stringResource

/**
 * Switches which language the conference's own text is read in.
 *
 * The glyph carries the state in stroke weight, never in opacity: the faded character would come
 * out at 2.37:1 against the band and miss the 3:1 a non-text control has to meet.
 */
@Composable
fun LanguageToggleButton(
    language: DisplayLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = when (language) {
                DisplayLanguage.Japanese -> KaigiIcons.Default.LanguageToggleJapanese
                DisplayLanguage.English -> KaigiIcons.Default.LanguageToggleEnglish
            },
            contentDescription = stringResource(
                when (language) {
                    DisplayLanguage.Japanese -> Res.string.show_in_english
                    DisplayLanguage.English -> Res.string.show_in_japanese
                },
            ),
            modifier = Modifier.size(LanguageToggleButtonDefaults.iconSize),
        )
    }
}

private object LanguageToggleButtonDefaults {
    // Two characters share this glyph's box, so the design draws it 1.75x a single-mark icon
    // rather than at the size the rest of the bar's controls share.
    val iconSize = 25.dp
}

@LocalePreviews
@Preview
@Composable
private fun LanguageToggleButtonPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(16.dp),
        ) {
            LanguageToggleButton(language = DisplayLanguage.Japanese, onClick = {})
            LanguageToggleButton(language = DisplayLanguage.English, onClick = {})
        }
    }
}
