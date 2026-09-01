package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

sealed interface SoilFallback {
    val suspenseFallback: @Composable context(SoilSuspenseContext) BoxScope.() -> Unit
    val errorFallback: @Composable context(SoilErrorContext) BoxScope.() -> Unit
}

object SoilFallbackDefaults {
    fun default(): SoilFallback = Default

    fun custom(
        suspenseFallback: @Composable context(SoilSuspenseContext) BoxScope.() -> Unit,
        errorFallback: @Composable context(SoilErrorContext) BoxScope.() -> Unit,
    ): SoilFallback = Custom(suspenseFallback, errorFallback)
}

private object Default : SoilFallback {
    override val suspenseFallback: @Composable context(SoilSuspenseContext) BoxScope.() -> Unit
        get() = { DefaultSuspenseFallbackContent() }
    override val errorFallback: @Composable context(SoilErrorContext) BoxScope.() -> Unit
        get() = { DefaultErrorFallbackContent() }
}

private class Custom(
    override val suspenseFallback: @Composable context(SoilSuspenseContext) BoxScope.() -> Unit,
    override val errorFallback: @Composable context(SoilErrorContext) BoxScope.() -> Unit,
) : SoilFallback

const val DEFAULT_SUSPENSE_FALLBACK_CONTENT_TEST_TAG = "DefaultSuspenseFallbackContentTestTag"

@Composable
context(_: SoilSuspenseContext)
fun DefaultSuspenseFallbackContent(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .testTag(DEFAULT_SUSPENSE_FALLBACK_CONTENT_TEST_TAG)
            .progressSemantics(),
    ) {
        LanternLoadingFallback()
    }
}

const val DEFAULT_ERROR_FALLBACK_CONTENT_TEST_TAG = "DefaultErrorFallbackContentTestTag"

@Composable
context(errorContext: SoilErrorContext)
fun DefaultErrorFallbackContent(
    modifier: Modifier = Modifier,
    scene: ErrorScene = ErrorSceneDefaults.sceneOfLaunch,
) {
    Box(
        modifier = modifier.fillMaxSize().testTag(DEFAULT_ERROR_FALLBACK_CONTENT_TEST_TAG),
        contentAlignment = Alignment.Center,
    ) {
        ErrorFallback(
            reset = errorContext.errorBoundaryContext.reset,
            scene = scene,
        )
    }
}
