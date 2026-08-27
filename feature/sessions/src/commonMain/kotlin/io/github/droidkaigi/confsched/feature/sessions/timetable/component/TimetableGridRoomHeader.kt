package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.RoomShape
import io.github.droidkaigi.confsched.core.designsystem.roomTheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

@Composable
internal fun TimetableGridRoomHeader(
    room: SessionRoom,
    modifier: Modifier = Modifier,
) {
    val theme = roomTheme(room)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TimetableGridHeaderHeight),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        ) {
            theme.shape?.let { TimetableGridRoomMark(shape = it, color = theme.accent) }
            Text(
                text = room.name,
                color = theme.accent,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(2.dp)
                .background(theme.accent),
        )
    }
}

@Composable
private fun TimetableGridRoomMark(shape: RoomShape, color: Color) {
    Canvas(modifier = Modifier.size(8.dp)) {
        val width = size.width
        val height = size.height
        when (shape) {
            RoomShape.Circle -> drawCircle(color)

            RoomShape.Square -> drawRect(color)

            RoomShape.Triangle -> drawPath(
                path = Path().apply {
                    moveTo(width / 2f, 0f)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                },
                color = color,
            )

            RoomShape.Diamond -> drawPath(
                path = Path().apply {
                    moveTo(width / 2f, 0f)
                    lineTo(width, height / 2f)
                    lineTo(width / 2f, height)
                    lineTo(0f, height / 2f)
                    close()
                },
                color = color,
            )

            RoomShape.Star -> {
                val center = Offset(width / 2f, height / 2f)
                drawPath(
                    path = Path().apply {
                        moveTo(center.x, 0f)
                        quadraticTo(center.x, center.y, width, center.y)
                        quadraticTo(center.x, center.y, center.x, height)
                        quadraticTo(center.x, center.y, 0f, center.y)
                        quadraticTo(center.x, center.y, center.x, 0f)
                        close()
                    },
                    color = color,
                )
            }
        }
    }
}

@LocalePreviews
@Composable
private fun TimetableGridRoomHeaderPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableGridRoomHeader(room = SessionRoom.NARWHAL)
    }
}
