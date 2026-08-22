package io.github.droidkaigi.confsched.feature.profilecard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.Res
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.hall
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.jellyfish
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.koala
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.ladybug
import io.github.droidkaigi.confsched.feature.profilecard.generated.resources.meerkat
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class Mascot {
    Hall,
    Jellyfish,
    Koala,
    Ladybug,
    Meerkat,
}

private val Mascot.drawableResource: DrawableResource
    get() = when (this) {
        Mascot.Hall -> Res.drawable.hall
        Mascot.Jellyfish -> Res.drawable.jellyfish
        Mascot.Koala -> Res.drawable.koala
        Mascot.Ladybug -> Res.drawable.ladybug
        Mascot.Meerkat -> Res.drawable.meerkat
    }

/**
 * Line art for a [Mascot], traced from the Figma source and shipped as SVG. The artwork is a
 * single flat stroke color in the source file, so [color] retints it via [ColorFilter.tint]
 * rather than the mascot needing per-theme variants of the file itself.
 */
@Composable
fun MascotIcon(
    mascot: Mascot,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Image(
        painter = painterResource(mascot.drawableResource),
        contentDescription = null,
        modifier = modifier,
        colorFilter = ColorFilter.tint(color),
    )
}

@Preview
@Composable
private fun MascotIconPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(modifier = Modifier.padding(16.dp)) {
            Mascot.entries.forEach { mascot ->
                MascotIcon(mascot = mascot, modifier = Modifier.padding(4.dp).size(48.dp))
            }
        }
    }
}
