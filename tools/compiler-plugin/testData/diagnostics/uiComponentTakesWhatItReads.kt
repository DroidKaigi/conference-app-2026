// FILE: SearchCard.kt
package io.github.droidkaigi.confsched.feature.search

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import io.github.droidkaigi.confsched.core.preview.KaigiPreviewWrapper

class SearchCardUiState(val title: String, val subtitle: String)

@Composable
fun SearchCard(<!UI_COMPONENT_IGNORES_PARAMETER_PROPERTIES!>uiState<!>: SearchCardUiState) {
    Text(uiState.title)
}

@Preview
@PreviewWrapper(wrapper = KaigiPreviewWrapper::class)
@Composable
private fun SearchCardPreview() {
    SearchCard(SearchCardUiState(title = "title", subtitle = "subtitle"))
}

// FILE: SearchRow.kt
package io.github.droidkaigi.confsched.feature.search

import androidx.compose.material3.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import io.github.droidkaigi.confsched.core.preview.KaigiPreviewWrapper

class SearchRowUiState(val title: String, val subtitle: String)

@Composable
fun SearchRow(uiState: SearchRowUiState) {
    Column {
        Text(uiState.title)
        Text(uiState.subtitle)
    }
}

@Preview
@PreviewWrapper(wrapper = KaigiPreviewWrapper::class)
@Composable
private fun SearchRowPreview() {
    SearchRow(SearchRowUiState(title = "title", subtitle = "subtitle"))
}

// FILE: SearchSummary.kt
package io.github.droidkaigi.confsched.feature.search

import androidx.compose.material3.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import io.github.droidkaigi.confsched.core.preview.KaigiPreviewWrapper

class SearchSummaryUiState(val title: String, val subtitle: String)

@Composable
fun SearchSummary(uiState: SearchSummaryUiState) {
    SearchSummaryLabel(uiState)
}

@Composable
fun SearchSummaryLabel(uiState: SearchSummaryUiState) {
    Column {
        Text(uiState.title)
        Text(uiState.subtitle)
    }
}

@Preview
@PreviewWrapper(wrapper = KaigiPreviewWrapper::class)
@Composable
private fun SearchSummaryPreview() {
    SearchSummary(SearchSummaryUiState(title = "title", subtitle = "subtitle"))
}
