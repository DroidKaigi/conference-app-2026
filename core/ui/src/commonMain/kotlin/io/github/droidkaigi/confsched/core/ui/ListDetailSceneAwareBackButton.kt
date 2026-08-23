package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.droidkaigi.confsched.core.designsystem.icon.Close
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.close
import org.jetbrains.compose.resources.stringResource

/**
 * A pane is closed rather than backed out of, so it takes the close mark and holds clear of the
 * boundary it shares with the pane beside it.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListDetailSceneAwareBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (LocalListDetailSceneScope.current == null) {
        KaigiTopAppBarBackButton(onClick = onClick, modifier = modifier)
        return
    }
    IconButton(
        onClick = onClick,
        modifier = modifier.padding(start = LocalPanePartitionSpacerSize.current),
    ) {
        Icon(
            imageVector = KaigiIcons.Default.Close,
            contentDescription = stringResource(Res.string.close),
            modifier = Modifier.size(KaigiIconButtonDefaults.iconSize),
        )
    }
}

@LocalePreviews
@Preview
@Composable
private fun ListDetailSceneAwareBackButtonPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        ListDetailSceneAwareBackButton(onClick = {})
    }
}
