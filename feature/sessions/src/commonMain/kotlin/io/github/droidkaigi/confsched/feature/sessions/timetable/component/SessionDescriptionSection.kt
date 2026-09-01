package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.SCREEN_PREVIEW_WIDTH_DP
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiButton
import io.github.droidkaigi.confsched.core.ui.KaigiButtonDefaults
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.description
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.show_less
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.show_more
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailScreenUiState.DescriptionDisplay
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SessionDescriptionSection(
    description: String,
    descriptionDisplay: DescriptionDisplay,
    seed: Int,
    onDescriptionTruncationChange: (isTruncated: Boolean) -> Unit,
    onExpansionToggleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SessionSectionLabel(text = stringResource(Res.string.description))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = if (descriptionDisplay == DescriptionDisplay.Truncatable.Expanded) {
                Int.MAX_VALUE
            } else {
                SessionDescriptionSectionDefaults.COLLAPSED_LINES
            },
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result -> onDescriptionTruncationChange(result.hasVisualOverflow) },
        )
        if (descriptionDisplay is DescriptionDisplay.Truncatable) {
            KaigiButton(onClick = onExpansionToggleClick, seed = seed, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(
                        when (descriptionDisplay) {
                            DescriptionDisplay.Truncatable.Expanded -> Res.string.show_less
                            DescriptionDisplay.Truncatable.Collapsed -> Res.string.show_more
                        },
                    ),
                    style = KaigiButtonDefaults.labelStyle,
                )
            }
        }
    }
}

private object SessionDescriptionSectionDefaults {
    const val COLLAPSED_LINES = 7
}

@LocalePreviews
@Composable
private fun SessionDescriptionSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SessionDescriptionSection(
            description = "本セッションでは、サンプルアプリの設計とその変遷をたどります。" +
                "画面を構成する各層の役割分担、状態管理の方針、そしてマルチプラットフォーム対応で直面した課題と、" +
                "その解決に至るまでの試行錯誤を順に紹介します。前半ではアーキテクチャ全体を俯瞰し、" +
                "依存関係の整理やモジュール分割の考え方を説明します。後半では実際のコードを交えながら、" +
                "テスト戦略やビルド時間の改善など、開発を続けるうえで効いてきた工夫を取り上げます。" +
                "折りたたみ表示と「もっとみる」の挙動を確かめられるだけの長さを持たせたプレースホルダーの本文です。",
            descriptionDisplay = DescriptionDisplay.Truncatable.Collapsed,
            onDescriptionTruncationChange = {},
            seed = 630,
            onExpansionToggleClick = {},
            // Framed at phone width so the collapsed sample overflows and the toggle shows.
            modifier = Modifier.width(SCREEN_PREVIEW_WIDTH_DP.dp).padding(24.dp),
        )
    }
}
