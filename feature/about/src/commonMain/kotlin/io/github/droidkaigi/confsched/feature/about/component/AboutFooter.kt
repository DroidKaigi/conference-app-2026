package io.github.droidkaigi.confsched.feature.about.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.feature.about.generated.resources.Res
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_android_trademark
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_social_medium
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_social_x
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_social_youtube
import io.github.droidkaigi.confsched.feature.about.generated.resources.about_version_format
import io.github.droidkaigi.confsched.feature.about.generated.resources.ic_social_medium
import io.github.droidkaigi.confsched.feature.about.generated.resources.ic_social_x
import io.github.droidkaigi.confsched.feature.about.generated.resources.ic_social_youtube
import io.github.droidkaigi.confsched.feature.about.generated.resources.img_about_footer_character
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AboutFooter(
    versionName: String,
    onOpenYoutube: () -> Unit,
    onOpenX: () -> Unit,
    onOpenMedium: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().padding(top = 32.dp, bottom = 24.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // The marks belong to their platforms, so they keep the ground baked into the asset
                // rather than taking a scheme colour that inverts under the darker themes.
                Image(
                    painter = painterResource(Res.drawable.ic_social_youtube),
                    contentDescription = stringResource(Res.string.about_social_youtube),
                    modifier = Modifier.size(48.dp).clickable(onClick = onOpenYoutube),
                )
                Image(
                    painter = painterResource(Res.drawable.ic_social_x),
                    contentDescription = stringResource(Res.string.about_social_x),
                    modifier = Modifier.size(48.dp).clickable(onClick = onOpenX),
                )
                Image(
                    painter = painterResource(Res.drawable.ic_social_medium),
                    contentDescription = stringResource(Res.string.about_social_medium),
                    modifier = Modifier.size(48.dp).clickable(onClick = onOpenMedium),
                )
            }
            Icon(
                painter = painterResource(Res.drawable.img_about_footer_character),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .size(width = 30.dp, height = 36.dp),
            )
        }
        Text(
            text = stringResource(Res.string.about_version_format, versionName),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        )
        Text(
            text = stringResource(Res.string.about_android_trademark),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = MaterialTheme.typography.displaySmall.fontFamily,
                fontSize = 9.sp,
                lineHeight = 13.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        )
    }
}

@LocalePreviews
@Composable
private fun AboutFooterPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        AboutFooter(
            versionName = "1.0.0",
            onOpenYoutube = {},
            onOpenX = {},
            onOpenMedium = {},
        )
    }
}
