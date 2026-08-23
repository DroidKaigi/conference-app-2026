package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.roomTheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.Speaker
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.PreviewImage
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.fake
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.KaigiAvatar
import io.github.droidkaigi.confsched.core.ui.current
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.Res
import io.github.droidkaigi.confsched.feature.sessions.generated.resources.speaker_overflow
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TimetableGridCell(
    title: MultiLangText,
    room: Room,
    speakers: PersistentList<Speaker>,
    startsAt: String,
    endsAt: String,
    height: Dp,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = roomTheme(room)
    val shape = RoundedCornerShape(8.dp)
    val bucket = timetableGridBlockBucket(startsAt = startsAt, endsAt = endsAt)
    val detailColor = theme.onContainer.copy(alpha = TimetableGridCellDetailAlpha)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(theme.container)
            .border(width = 1.2.dp, color = theme.onContainer, shape = shape)
            .clickable(onClick = onItemClick)
            .padding(horizontal = 8.dp)
            .padding(top = bucket.topPadding),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(bucket.titleSpacing),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title.current(),
                color = theme.onContainer,
                style = MaterialTheme.typography.labelMedium,
                maxLines = bucket.titleMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
            if (bucket == TimetableGridBlockBucket.Short) {
                SpeakerRow(speakers = speakers, color = detailColor) {
                    CellDetail(text = startsAt, color = detailColor)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CellDetail(text = "$startsAt - $endsAt", color = detailColor)
                    // Only the tallest block has the room to name everyone; the others
                    // name the first and count the rest.
                    if (bucket == TimetableGridBlockBucket.Tall) {
                        speakers.forEach { SpeakerRow(speakers = persistentListOf(it), color = detailColor) }
                    } else {
                        SpeakerRow(speakers = speakers, color = detailColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeakerRow(
    speakers: PersistentList<Speaker>,
    color: Color,
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit = {},
) {
    val speaker = speakers.firstOrNull() ?: return
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading()
        SpeakerFace(iconUrl = speaker.iconUrl)
        CellDetail(text = speaker.name, color = color, modifier = Modifier.weight(1f))
        if (speakers.size > 1) {
            CellDetail(text = stringResource(Res.string.speaker_overflow, speakers.size - 1), color = color)
        }
    }
}

/**
 * The face standing in for a speaker, drawn to the design's own fill and ink.
 *
 * The outline is even rather than sketched: a wobbling one reads as the face itself being
 * misshapen, and at this size the sketch outline degrades into corners.
 */
@Composable
private fun SpeakerFace(iconUrl: String?, modifier: Modifier = Modifier) {
    if (iconUrl != null) {
        KaigiAvatar(
            imageUrl = iconUrl,
            contentDescription = null,
            size = SpeakerFaceSize,
            modifier = modifier,
        )
        return
    }
    val shape = RoundedCornerShape(percent = SPEAKER_FACE_CORNER_PERCENT)
    val fill = MaterialTheme.colorScheme.secondaryContainer
    val ink = MaterialTheme.colorScheme.onSecondaryContainer
    Canvas(
        modifier = modifier
            .size(SpeakerFaceSize)
            .clip(shape)
            .background(fill)
            .border(width = SpeakerFaceOutline, color = ink, shape = shape),
    ) {
        val featureStroke = Stroke(width = SpeakerFaceFeature.toPx(), cap = StrokeCap.Round)
        val w = size.width
        val h = size.height
        listOf(0.34f, 0.66f).forEach { eyeX ->
            drawPath(
                path = Path().apply {
                    moveTo(w * eyeX, h * 0.38f)
                    quadraticTo(w * (eyeX + 0.04f), h * 0.43f, w * eyeX, h * 0.48f)
                },
                color = ink,
                style = featureStroke,
            )
        }
        drawPath(
            path = Path().apply {
                moveTo(w * 0.33f, h * 0.61f)
                quadraticTo(w * 0.5f, h * 0.75f, w * 0.67f, h * 0.61f)
            },
            color = ink,
            style = featureStroke,
        )
    }
}

@Composable
private fun CellDetail(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

private const val TimetableGridCellDetailAlpha = 0.7f

private const val SPEAKER_FACE_CORNER_PERCENT = 34
private val SpeakerFaceSize = 16.dp
private val SpeakerFaceOutline = 0.3.dp
private val SpeakerFaceFeature = 0.9.dp


private fun fakePreviewSpeaker(name: String) = Speaker(
    name = name,
    iconUrl = PreviewImage.SpeakerAvatarA.imageUrl,
)

@LocalePreviews
@Composable
private fun TimetableGridCellPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        val item = TimetableItem.fake()
        TimetableGridCell(
            title = item.title,
            room = item.room,
            speakers = item.speakers,
            startsAt = item.startsAt,
            endsAt = item.endsAt,
            height = timetableGridSessionHeight(startsAt = item.startsAt, endsAt = item.endsAt),
            onItemClick = {},
            modifier = Modifier.width(TimetableGridRoomColumnWidth),
        )
    }
}

@LocalePreviews
@Composable
private fun TimetableGridTallCellPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableGridCell(
            title = MultiLangText(
                ja = "長めのセッションタイトルが三行まで入る場合の見え方",
                en = "A long session title that runs to three lines in the block",
            ),
            room = Room.QUAIL,
            speakers = persistentListOf(
                fakePreviewSpeaker("Speaker C"),
                fakePreviewSpeaker("Speaker D"),
            ),
            startsAt = "13:00",
            endsAt = "14:00",
            height = timetableGridSessionHeight(startsAt = "13:00", endsAt = "14:00"),
            onItemClick = {},
            modifier = Modifier.width(TimetableGridRoomColumnWidth),
        )
    }
}

@LocalePreviews
@Composable
private fun TimetableGridShortCellPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableGridCell(
            title = MultiLangText(
                ja = "短いセッション",
                en = "Short session with a long enough title",
            ),
            room = Room.NARWHAL,
            speakers = persistentListOf(
                fakePreviewSpeaker("Speaker A"),
                fakePreviewSpeaker("Speaker B"),
                fakePreviewSpeaker("Speaker C"),
            ),
            startsAt = "10:00",
            endsAt = "10:20",
            height = timetableGridSessionHeight(startsAt = "10:00", endsAt = "10:20"),
            onItemClick = {},
            modifier = Modifier.width(TimetableGridRoomColumnWidth),
        )
    }
}
