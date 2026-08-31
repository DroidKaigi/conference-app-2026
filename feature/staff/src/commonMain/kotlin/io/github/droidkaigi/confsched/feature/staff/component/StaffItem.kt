package io.github.droidkaigi.confsched.feature.staff.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.PreviewImage
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiAvatar

@Composable
internal fun StaffItem(
    username: String,
    iconUrl: String,
    onStaffClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onStaffClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        KaigiAvatar(
            imageUrl = iconUrl,
            contentDescription = null,
            size = StaffItemDefaults.avatarSize,
        )
        Text(
            text = username,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

internal object StaffItemDefaults {
    val avatarSize = 100.dp
}

@Preview
@Composable
private fun StaffItemPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        StaffItem(
            username = "user-a",
            iconUrl = PreviewImage.AvatarSample.imageUrl,
            onStaffClick = {},
        )
    }
}
