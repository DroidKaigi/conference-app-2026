package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.icon.KaigiIcons
import io.github.droidkaigi.confsched.core.designsystem.icon.Person
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiPlaceholderAvatar

@Composable
internal fun TimetableItemDetailHeadline(
    room: String,
    title: String,
    speaker: String,
    startInset: Dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(start = startInset)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = room.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KaigiPlaceholderAvatar(
                seed = TimetableItemDetailHeadlineDefaults.AVATAR_SEED,
                size = TimetableItemDetailHeadlineDefaults.avatarSize,
            ) {
                Icon(
                    imageVector = KaigiIcons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(TimetableItemDetailHeadlineDefaults.avatarIconSize),
                )
            }
            Text(
                text = speaker,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

private object TimetableItemDetailHeadlineDefaults {
    val avatarSize = 52.dp
    val avatarIconSize = 24.dp
    const val AVATAR_SEED = 862
}

@Preview
@Composable
private fun TimetableItemDetailHeadlinePreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableItemDetailHeadline(
            room = "NARWHAL",
            title = "Sample Session A",
            speaker = "Speaker A",
            startInset = 0.dp,
        )
    }
}
