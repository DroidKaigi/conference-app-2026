package io.github.droidkaigi.confsched.feature.eventmap.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.PreviewImage
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.RemoteImage
import io.github.droidkaigi.confsched.core.ui.SketchRoundRectShape

@Composable
internal fun PrizeCardItem(
    name: String,
    imageUrl: String,
    seed: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SketchCard(
        shape = SketchRoundRectShape(seed = seed, cornerRadius = 20.dp, borderThickness = 2.dp),
        modifier = modifier.clickable(role = Role.Button, onClick = onClick),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    // Prize photos are shot on white, so the slot keeps true white in every theme.
                    .background(color = Color.White, shape = RoundedCornerShape(12.dp)),
            ) {
                RemoteImage(
                    imageUrl = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@LocalePreviews
@Composable
private fun PrizeCardItemPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        PrizeCardItem(
            name = "Prize 1",
            imageUrl = PreviewImage.PrizePhoto.imageUrl,
            seed = 210,
            onClick = {},
            modifier = Modifier
                .padding(16.dp)
                .width(182.dp),
        )
    }
}
