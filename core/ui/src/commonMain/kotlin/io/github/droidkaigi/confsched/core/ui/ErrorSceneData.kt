package io.github.droidkaigi.confsched.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.PathParser

internal enum class SceneRole {
    Primary,
    OnPrimary,
    OnSurface,
    PrimaryContainer,
}

internal class ScenePath(
    val data: String,
    val role: SceneRole,
    val fill: Boolean,
    val strokeWidth: Float = 0f,
    val roundCap: Boolean = false,
    val roundJoin: Boolean = false,
    val alpha: Float = 1f,
)

// The design draws every scene in theme tokens, so the colors arrive from the theme instead of
// being baked into the path data.
internal class SceneColors(
    val primary: Color,
    val onPrimary: Color,
    val onSurface: Color,
    val primaryContainer: Color,
) {
    fun resolve(role: SceneRole): Color = when (role) {
        SceneRole.Primary -> primary
        SceneRole.OnPrimary -> onPrimary
        SceneRole.OnSurface -> onSurface
        SceneRole.PrimaryContainer -> primaryContainer
    }
}

internal class RenderedScenePath(
    val path: Path,
    val source: ScenePath,
)

internal fun List<ScenePath>.render(): List<RenderedScenePath> =
    map { RenderedScenePath(PathParser().parsePathString(it.data).toPath(), it) }

internal fun DrawScope.drawScenePaths(
    paths: List<RenderedScenePath>,
    colors: SceneColors,
) {
    for (rendered in paths) {
        val source = rendered.source
        val color = colors.resolve(source.role)
        if (source.fill) {
            drawPath(rendered.path, color, alpha = source.alpha)
        } else {
            drawPath(
                path = rendered.path,
                color = color,
                alpha = source.alpha,
                style = Stroke(
                    width = source.strokeWidth,
                    cap = if (source.roundCap) StrokeCap.Round else StrokeCap.Butt,
                    join = if (source.roundJoin) StrokeJoin.Round else StrokeJoin.Miter,
                ),
            )
        }
    }
}

// The 412x892 frame the scenes are authored against; the wave edges sit on the 596 line, where
// the text zone starts.
internal const val SCENE_FRAME_WIDTH = 412f
internal const val SCENE_FRAME_HEIGHT = 892f
