package io.github.droidkaigi.confsched.feature.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.ArrowBack
import io.github.droidkaigi.confsched.core.designsystem.icon.Close
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Search
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.SketchDefaults
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape
import io.github.droidkaigi.confsched.core.ui.combineSketchSeed
import io.github.droidkaigi.confsched.core.ui.sketchBorder
import io.github.droidkaigi.confsched.feature.search.generated.resources.Res
import io.github.droidkaigi.confsched.feature.search.generated.resources.search
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_back
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_clear
import io.github.droidkaigi.confsched.feature.search.generated.resources.search_hint
import org.jetbrains.compose.resources.stringResource

internal const val SEARCH_TOP_BAR_BACK_BUTTON_TEST_TAG = "SearchTopBarBackButtonTestTag"
internal const val SEARCH_TOP_BAR_QUERY_FIELD_TEST_TAG = "SearchTopBarQueryFieldTestTag"
internal const val SEARCH_TOP_BAR_CLEAR_BUTTON_TEST_TAG = "SearchTopBarClearButtonTestTag"

@Composable
internal fun SearchTopBar(
    queryText: String,
    onQueryTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.inverseSurface)
            .windowInsetsPadding(TopAppBarDefaults.windowInsets)
            .padding(
                horizontal = 16.dp,
                vertical = 11.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SearchBackButton(
            onClick = onBackClick,
            modifier = Modifier.size(38.dp),
        )
        SearchQueryField(
            queryText = queryText,
            onQueryTextChange = onQueryTextChange,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SearchBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .testTag(SEARCH_TOP_BAR_BACK_BUTTON_TEST_TAG)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(9.dp),
    ) {
        Icon(
            imageVector = KaigiIcons.Default.ArrowBack,
            contentDescription = stringResource(Res.string.search_back),
            tint = MaterialTheme.colorScheme.inverseOnSurface,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SearchQueryField(
    queryText: String,
    onQueryTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(focusRequester) { focusRequester.requestFocus() }
    val searchDescription = stringResource(Res.string.search)

    // BasicTextField's String overload places a restored query's cursor at the start.
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = queryText,
                selection = TextRange(queryText.length),
            ),
        )
    }
    var pendingQueryText by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(queryText) {
        when {
            queryText == textFieldValue.text -> pendingQueryText = null

            pendingQueryText == null -> textFieldValue = TextFieldValue(
                text = queryText,
                selection = TextRange(queryText.length),
            )
        }
    }

    val shape = SketchRoundRectShape(
        seed = combineSketchSeed(831),
        roughness = SketchDefaults.roughness,
        tremor = SketchDefaults.tremor,
        cornerRadius = 12.dp,
        borderThickness = 1.5.dp,
    )
    BasicTextField(
        value = textFieldValue,
        onValueChange = { newTextFieldValue ->
            val textChanged = newTextFieldValue.text != textFieldValue.text
            textFieldValue = newTextFieldValue
            if (textChanged) {
                pendingQueryText = newTextFieldValue.text
                onQueryTextChange(newTextFieldValue.text)
            }
        },
        modifier = modifier
            .height(40.dp)
            .testTag(SEARCH_TOP_BAR_QUERY_FIELD_TEST_TAG)
            .focusRequester(focusRequester)
            .semantics { contentDescription = searchDescription },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
    ) { innerTextField ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, shape)
                    .sketchBorder(shape, MaterialTheme.colorScheme.outline)
                    .clip(shape),
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 12.dp,
                        end = if (textFieldValue.text.isEmpty()) 12.dp else 0.dp,
                    ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = KaigiIcons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.search_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    innerTextField()
                }
                if (textFieldValue.text.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            textFieldValue = TextFieldValue()
                            pendingQueryText = ""
                            onQueryTextChange("")
                        },
                        modifier = Modifier.testTag(SEARCH_TOP_BAR_CLEAR_BUTTON_TEST_TAG),
                    ) {
                        Icon(
                            imageVector = KaigiIcons.Default.Close,
                            contentDescription = stringResource(Res.string.search_clear),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
        }
    }
}

@LocalePreviews
@Composable
private fun SearchTopBarPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SearchTopBar(queryText = "Compose", onQueryTextChange = {}, onBackClick = {})
    }
}

@LocalePreviews
@Composable
private fun SearchTopBarEmptyPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SearchTopBar(queryText = "", onQueryTextChange = {}, onBackClick = {})
    }
}
