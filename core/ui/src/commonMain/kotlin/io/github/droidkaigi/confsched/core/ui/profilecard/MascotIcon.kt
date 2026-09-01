package io.github.droidkaigi.confsched.core.ui.profilecard

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
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_a
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_b
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_c
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_d
import io.github.droidkaigi.confsched.core.ui.generated.resources.mascot_e
import io.github.droidkaigi.confsched.core.ui.mascotFArt
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val Mascot.drawableResource: DrawableResource
    get() = when (this) {
        Mascot.A -> Res.drawable.mascot_a
        Mascot.B -> Res.drawable.mascot_b
        Mascot.C -> Res.drawable.mascot_c
        Mascot.D -> Res.drawable.mascot_d
        Mascot.E -> Res.drawable.mascot_e
        Mascot.F -> mascotFArt
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
