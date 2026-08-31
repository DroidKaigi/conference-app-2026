package io.github.droidkaigi.confsched.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector

enum class ErrorScene {
    UnpluggedCable,
    Rain,
    Backstage,
}

object ErrorSceneDefaults {
    // The design fixes one scene per launch: picked at random on first use, then kept.
    val sceneOfLaunch: ErrorScene by lazy(ErrorScene.entries::random)
}

// The design draws every scene in theme tokens, so the colors arrive from the theme instead of
// being baked into the path data.
@Composable
fun rememberErrorSceneVector(scene: ErrorScene): ImageVector {
    val colorScheme = MaterialTheme.colorScheme
    return remember(scene, colorScheme) {
        when (scene) {
            ErrorScene.UnpluggedCable -> unpluggedCableSceneVector(
                primary = colorScheme.primary,
                onPrimary = colorScheme.onPrimary,
            )
            ErrorScene.Rain -> rainSceneVector(
                primary = colorScheme.primary,
                onSurface = colorScheme.onSurface,
                primaryContainer = colorScheme.primaryContainer,
            )
            ErrorScene.Backstage -> backstageSceneVector(
                primary = colorScheme.primary,
                onSurface = colorScheme.onSurface,
                primaryContainer = colorScheme.primaryContainer,
                onPrimary = colorScheme.onPrimary,
            )
        }
    }
}
