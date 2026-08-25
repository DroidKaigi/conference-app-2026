package io.github.droidkaigi.confsched.feature.sponsors.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.PreviewImage
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.RemoteImage

@Composable
internal fun SponsorItem(
    name: String,
    logoUrl: String,
    onSponsorClick: () -> Unit,
    shape: Shape,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable(role = Role.Button, onClick = onSponsorClick),
        shape = shape,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
        // Sponsor logos ship with an opaque white background, so the card matches it regardless of theme.
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        RemoteImage(
            imageUrl = logoUrl,
            contentDescription = name,
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            // Logos arrive in arbitrary aspect ratios; cropping one to the card would cut off the wordmark.
            contentScale = ContentScale.Fit,
        )
    }
}

@Preview
@Composable
private fun SponsorItemPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        SponsorItem(
            name = "Sponsor A",
            logoUrl = PreviewImage.SessionCover.imageUrl,
            onSponsorClick = {},
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(12.dp),
        )
    }
}
