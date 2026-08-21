package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.designsystem.icon.Close
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiIconButton
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBar
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBarBackButton
import io.github.droidkaigi.confsched.feature.search.generated.resources.Res
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_clear
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_hint
import org.jetbrains.compose.resources.stringResource

/**
 * The search screen's bar: the query field where a screen naming itself puts its title, the back
 * arrow leading it, and a clear button trailing it once there is something to clear.
 */
@Composable
internal fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    // The screen exists to be typed into, so it opens with the field focused.
    LaunchedEffect(focusRequester) { focusRequester.requestFocus() }

    KaigiTopAppBar(
        title = {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.inverseOnSurface),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            ) { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.search_hint),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.inverseOnSurface.copy(
                                alpha = SearchTopBarDefaults.HINT_ALPHA,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    innerTextField()
                }
            }
        },
        modifier = modifier,
        navigationIcon = { KaigiTopAppBarBackButton(onClick = onBackClick) },
    ) {
        if (query.isNotEmpty()) {
            KaigiIconButton(seed = SearchTopBarDefaults.CLEAR_SEED, onClick = { onQueryChange("") }) {
                Icon(KaigiIcons.Default.Close, contentDescription = stringResource(Res.string.search_clear))
            }
        }
    }
}

private object SearchTopBarDefaults {
    const val HINT_ALPHA = 0.6f

    // The timetable's bar draws its actions from 777 and 778; a different seed keeps this pill's
    // outline from repeating one of theirs.
    const val CLEAR_SEED = 831
}

@LocalePreviews
@Composable
private fun SearchTopBarPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SearchTopBar(query = "Compose", onQueryChange = {}, onBackClick = {})
    }
}

@LocalePreviews
@Composable
private fun SearchTopBarEmptyPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SearchTopBar(query = "", onQueryChange = {}, onBackClick = {})
    }
}
