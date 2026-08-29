package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.common.toAppError
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.failed_to_load
import io.github.droidkaigi.confsched.core.ui.generated.resources.retry
import org.jetbrains.compose.resources.stringResource

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

@Composable
context(errorContext: SoilErrorContext)
fun DefaultErrorFallbackContent(modifier: Modifier = Modifier) {
    val boundary = errorContext.errorBoundaryContext
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(Res.string.failed_to_load))
            Text(boundary.err.toAppError().localizedMessage())
            boundary.reset?.let { reset ->
                Button(onClick = reset) {
                    Text(stringResource(Res.string.retry))
                }
            }
        }
    }
}
