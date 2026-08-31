package io.github.droidkaigi.confsched.feature.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.kaigiTypography
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.settings.generated.resources.Res
import io.github.droidkaigi.confsched.feature.settings.generated.resources.font
import io.github.droidkaigi.confsched.feature.settings.generated.resources.font_courier_prime
import io.github.droidkaigi.confsched.feature.settings.generated.resources.font_default
import io.github.droidkaigi.confsched.feature.settings.generated.resources.font_noto_sans
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FontFamilyGroupSection(
    fontFamily: KaigiFontFamily,
    onFontFamilyClick: (KaigiFontFamily) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsGroupSection(title = stringResource(Res.string.font), modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KaigiFontFamily.entries.forEachIndexed { index, entry ->
                FontFamilyOptionItem(
                    label = stringResource(entry.label),
                    labelStyle = entry.labelStyle,
                    selected = entry == fontFamily,
                    seed = FontFamilyGroupDefaults.FIRST_OPTION_SEED + index,
                    onClick = { onFontFamilyClick(entry) },
                    modifier = Modifier.testTag(fontFamilyOptionItemTestTag(entry)),
                )
            }
        }
    }
}

private val KaigiFontFamily.label: StringResource
    get() = when (this) {
        KaigiFontFamily.Default -> Res.string.font_default
        KaigiFontFamily.CourierPrime -> Res.string.font_courier_prime
        KaigiFontFamily.NotoSans -> Res.string.font_noto_sans
    }

// Each option names itself in the face it selects, so the style comes from the type set that
// option installs rather than from the one the preference in force has already installed.
private val KaigiFontFamily.labelStyle: TextStyle
    @Composable get() = kaigiTypography(this).titleLarge

private object FontFamilyGroupDefaults {
    const val FIRST_OPTION_SEED = 5810
}

@LocalePreviews
@Composable
private fun FontFamilyGroupSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        FontFamilyGroupSection(
            fontFamily = KaigiFontFamily.Default,
            onFontFamilyClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
