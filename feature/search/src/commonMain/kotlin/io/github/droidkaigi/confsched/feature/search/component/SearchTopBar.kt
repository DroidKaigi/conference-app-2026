package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.Close
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Search
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiTopAppBarBackButton
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.search.generated.resources.Res
import io.github.droidkaigi.confsched.feature.search.generated.resources.search
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_clear
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_hint
import org.jetbrains.compose.resources.stringResource

/**
 * The search screen's header: the back arrow beside a field the query is typed into.
 *
 * The field carries its own fill and sketched outline rather than sitting bare on the band, so it
 * reads as somewhere to type before it is focused.
 */
@Composable
internal fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.inverseSurface)
            .padding(horizontal = 8.dp, vertical = SearchTopBarDefaults.bandPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KaigiTopAppBarBackButton(onClick = onBackClick)
        SearchQueryField(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.weight(1f),
        )
    }
}

/** The field itself: a sketched pill led by a magnifier, with a clear button once it has content. */
@Composable
private fun SearchQueryField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }
    // The screen exists to be typed into, so it opens with the field focused.
    LaunchedEffect(focusRequester) { focusRequester.requestFocus() }

    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(SearchTopBarDefaults.FIELD_SEED),
        cornerRadius = SearchTopBarDefaults.fieldCornerRadius,
        borderThickness = SearchTopBarDefaults.fieldBorderThickness,
    )
    Row(
        modifier = modifier
            .height(SearchTopBarDefaults.fieldHeight)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, shape)
            .sketchBorder(shape, MaterialTheme.colorScheme.outline)
            .clip(shape)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = KaigiIcons.Default.Search,
            contentDescription = stringResource(Res.string.search),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(SearchTopBarDefaults.leadingIconSize),
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f).focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        ) { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.search_hint),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                innerTextField()
            }
        }
        if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(SearchTopBarDefaults.clearSize)) {
                Icon(
                    imageVector = KaigiIcons.Default.Close,
                    contentDescription = stringResource(Res.string.search_clear),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(SearchTopBarDefaults.leadingIconSize),
                )
            }
        }
    }
}

private object SearchTopBarDefaults {
    val bandPadding = 12.dp
    val fieldHeight = 40.dp
    val fieldCornerRadius = 20.dp
    val fieldBorderThickness = 1.5.dp
    val leadingIconSize = 18.dp
    val clearSize = 24.dp

    // The timetable's bar draws its actions from 777 and 778; a different seed keeps this outline
    // from repeating one of theirs.
    const val FIELD_SEED = 831
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
