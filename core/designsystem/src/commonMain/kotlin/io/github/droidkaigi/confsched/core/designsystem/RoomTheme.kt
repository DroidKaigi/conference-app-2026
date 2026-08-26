package io.github.droidkaigi.confsched.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import io.github.droidkaigi.confsched.core.model.Room

/** The mark a room is identified by, drawn beside its name. */
enum class RoomShape { Circle, Star, Square, Triangle, Diamond }

/**
 * The palette a room carries wherever it appears: its chip, and anything tinted to match.
 *
 * [container] and [onContainer] hold across all five schemes: the chip is filled with a
 * fixed light color, so the label and the mark on it always want the darker reading.
 *
 * [accent] is what tints the session itself — the bookmark heart — which sits on the card
 * rather than on the chip. That ground follows the scheme, so the accent takes a lighter
 * reading where the scheme is dark.
 *
 * [shape] is null for a room the design has not placed, which then draws under its name alone.
 */
@Immutable
data class RoomTheme(
    val container: Color,
    val onContainer: Color,
    val accent: Color,
    val shape: RoomShape?,
)

/**
 * The palette for [room].
 *
 * The when is exhaustive, so a room added to [Room] does not compile until it is given a palette.
 */
@Composable
@ReadOnlyComposable
fun roomTheme(room: Room): RoomTheme = roomTheme(room, isDark = LocalSchemeIsDark.current)

/** The palette for [room] under a scheme of the given darkness, for callers outside a composition such as widgets. */
fun roomTheme(room: Room, isDark: Boolean): RoomTheme {
    return when (room) {
        Room.NARWHAL -> RoomTheme(
            container = Color(0xFFE2DCFE),
            onContainer = Color(0xFF3F2296),
            accent = if (isDark) Color(0xFFB8A3E4) else Color(0xFF7B58CB),
            shape = RoomShape.Circle,
        )

        Room.OTTER -> RoomTheme(
            container = Color(0xFFF0DCF8),
            onContainer = Color(0xFF6A1B9A),
            accent = if (isDark) Color(0xFFD18ADE) else Color(0xFFA341BD),
            shape = RoomShape.Star,
        )

        Room.PANDA -> RoomTheme(
            container = Color(0xFFE3E9FB),
            onContainer = Color(0xFF3949AB),
            accent = if (isDark) Color(0xFF9AA7E0) else Color(0xFF5566C4),
            shape = RoomShape.Square,
        )

        Room.QUAIL -> RoomTheme(
            container = Color(0xFFD8F6E8),
            onContainer = Color(0xFF1B5E20),
            accent = if (isDark) Color(0xFF66BB6A) else Color(0xFF2E7D32),
            shape = RoomShape.Triangle,
        )

        Room.MEERKAT -> RoomTheme(
            container = Color(0xFFDCF9FD),
            onContainer = Color(0xFF005A63),
            accent = if (isDark) Color(0xFF4DD0E0) else Color(0xFF00838F),
            shape = RoomShape.Diamond,
        )

        // Neutral rather than one of the five: a room the design has not placed must not
        // arrive wearing another room's color and mark.
        Room.UNKNOWN -> RoomTheme(
            container = Color(0xFFE6E6EA),
            onContainer = Color(0xFF3F3F46),
            accent = if (isDark) Color(0xFFB4B4BE) else Color(0xFF5A5A63),
            shape = null,
        )
    }
}
