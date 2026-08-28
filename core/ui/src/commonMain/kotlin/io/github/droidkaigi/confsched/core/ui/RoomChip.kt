package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.designsystem.RoomShape
import io.github.droidkaigi.confsched.core.designsystem.roomTheme
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.MultiLangText
import io.github.droidkaigi.confsched.core.model.NamedRoom
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.SessionRoom
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme

/** The room an event is in, named in the room's own color and marked where the room has a mark. */
@Composable
fun RoomChip(room: Room, seed: Int) {
    val theme = roomTheme(room)
    KaigiChip(seed = seed, containerColor = theme.container, contentColor = theme.onContainer) {
        theme.shape?.let { RoomMark(shape = it, color = theme.onContainer) }
        Text(text = room.label.current(), style = KaigiChipDefaults.labelStyle)
    }
}

@Composable
private fun RoomMark(shape: RoomShape, color: Color) {
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
private fun RoomChipPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            RoomChip(room = SessionRoom.NARWHAL, seed = 1)
            RoomChip(room = SessionRoom.QUAIL, seed = 2)
            RoomChip(room = SessionRoom.MEERKAT, seed = 3)
            RoomChip(room = NamedRoom(MultiLangText(ja = "ホワイエ", en = "Foyer")), seed = 4)
        }
    }
}
