package io.github.droidkaigi.confsched.feature.about.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Edit
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.AboutHeroHeight
import io.github.droidkaigi.confsched.core.ui.AboutHeroStageTopInset
import io.github.droidkaigi.confsched.core.ui.AboutHeroStageWidth
import io.github.droidkaigi.confsched.core.ui.DoodleLayerView
import io.github.droidkaigi.confsched.core.ui.DoodleOrigin
import io.github.droidkaigi.confsched.core.ui.KaigiChip
import io.github.droidkaigi.confsched.core.ui.KaigiChipDefaults
import io.github.droidkaigi.confsched.core.ui.SketchBottomEdgeShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.rememberAboutHeroStage
import io.github.droidkaigi.confsched.core.ui.scaleSketchAmplitude
import io.github.droidkaigi.confsched.core.ui.sketchBottomEdge
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_doodle
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_draw_on_the_wall
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_logo_description
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AboutHero(
    doodle: Doodle,
    onEditDoodleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth().height(AboutHeroHeight)) {
        val wallShape = SketchBottomEdgeShape(
            seed = combineSketchSeed(WALL_EDGE_SEED),
            roughness = scaleSketchAmplitude(8.5.dp),
            tremor = scaleSketchAmplitude(1.dp),
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
                .padding(top = AboutHeroStageTopInset)
                .fillMaxWidth()
                .widthIn(max = AboutHeroStageWidth),
        )
        DoodleLayerView(
            doodle = doodle,
            color = MaterialTheme.colorScheme.onPrimary,
            haloColor = null,
            origin = DoodleOrigin.TopCenter,
            scale = 1f,
            modifier = Modifier.matchParentSize(),
        )
        DoodleSignChip(
            onClick = onEditDoodleClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = SignChipInset, top = SignChipTopInset),
        )
    }
}

@Composable
private fun DoodleSignChip(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(Res.string.about_draw_on_the_wall)
    KaigiChip(
        seed = SIGN_CHIP_SEED,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label },
    ) {
        Icon(
            imageVector = KaigiIcons.Default.Edit,
            contentDescription = null,
            modifier = Modifier.size(SignChipIconSize),
        )
        Text(text = stringResource(Res.string.about_doodle), style = KaigiChipDefaults.labelStyle)
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

private const val WALL_EDGE_SEED = 5573
private const val SIGN_CHIP_SEED = 5574
private val WallHeight = 241.dp
private val SignChipIconSize = 14.dp
private val SignChipInset = 12.dp
private val SignChipTopInset = 16.dp

@LocalePreviews
@Composable
private fun AboutHeroPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutHero(doodle = Doodle.Empty, onEditDoodleClick = {})
    }
}

@LocalePreviews
@Composable
private fun AboutHeroWithDoodlePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutHero(doodle = Doodle.fake(), onEditDoodleClick = {})
    }
}
