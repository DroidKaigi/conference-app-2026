package io.github.droidkaigi.confsched.feature.about.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchBottomEdgeShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.scaleSketchAmplitude
import io.github.droidkaigi.confsched.core.ui.sketchBottomEdge
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_logo_description
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AboutHero(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().height(HeroHeight)) {
        val wallShape = SketchBottomEdgeShape(
            seed = combineSketchSeed(WALL_EDGE_SEED),
            roughness = scaleSketchAmplitude(1.6.dp),
            tremor = scaleSketchAmplitude(0.4.dp),
            sweepWavelength = 200.dp,
            tremorWavelength = 42.dp,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WallHeight)
                .background(color = MaterialTheme.colorScheme.primary, shape = wallShape)
                .sketchBottomEdge(
                    shape = wallShape,
                    color = MaterialTheme.colorScheme.onPrimary,
                    thickness = 1.5.dp,
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .height(11.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            ) {
                BattenEdge()
                Spacer(modifier = Modifier.weight(1f))
                BattenEdge()
            }
        }
        Image(
            imageVector = rememberAboutHeroStage(),
            contentDescription = stringResource(Res.string.about_logo_description),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 15.dp)
                .fillMaxWidth()
                .widthIn(max = StageWidth),
        )
    }
}

@Composable
private fun BattenEdge() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.4.dp)
            .background(MaterialTheme.colorScheme.onPrimaryContainer),
    )
}

private const val WALL_EDGE_SEED = 2280
private val WallHeight = 241.dp
private val StageWidth = 331.dp

/** Tall enough for the mascots standing at the foot of the stage to clear the wall's lower edge. */
private val HeroHeight = 246.dp

@LocalePreviews
@Composable
private fun AboutHeroPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutHero()
    }
}
