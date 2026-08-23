package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.Room
import io.github.droidkaigi.confsched.core.model.TimetableItem
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.preview.KaigiSchemeProvider
import io.github.droidkaigi.confsched.core.preview.LocalePreviews
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import io.github.droidkaigi.confsched.core.ui.LocalNavigationBarOccupiedHeight
import io.github.droidkaigi.confsched.core.ui.SketchHorizontalDivider
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.abs

@Composable
internal fun TimetableGridSection(
    uiState: TimetableGridSectionUiState,
    onItemClick: (TimetableItemId) -> Unit,
    initialHourHeight: Dp = TimetableGridDefaultHourHeight,
) {
    val rooms = TimetableGridDefaultRooms
    val endMinute = uiState.sessions
        .maxOfOrNull { it.endsAt.toTimetableGridMinuteOfDay() }
        ?.coerceAtLeast(TimetableGridDefaultDayEndMinutes)
        ?: TimetableGridDefaultDayEndMinutes
    val navigationBarHeight = LocalNavigationBarOccupiedHeight.current

    var hourHeightValue by rememberSaveable { mutableStateOf(initialHourHeight.value) }
    var pinchStartHourHeightValue by rememberSaveable { mutableStateOf(initialHourHeight.value) }
    var pinching by rememberSaveable { mutableStateOf(false) }
    val hourHeight by animateDpAsState(
        targetValue = hourHeightValue.dp,
        animationSpec = tween(durationMillis = if (pinching) 0 else 180),
        label = "TimetableGridHourHeight",
    )
    val contentHeight = timetableGridContentHeight(endMinute, hourHeight)
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .timetableGridZoom(
                onPinchStart = {
                    pinching = true
                    pinchStartHourHeightValue = hourHeightValue
                },
                onZoom = { zoomRatio ->
                    hourHeightValue = (pinchStartHourHeightValue * zoomRatio)
                        .coerceIn(TimetableGridDefaultHourHeight.value, TimetableGridMaxHourHeight.value)
                },
                onPinchEnd = {
                    pinching = false
                    hourHeightValue = timetableGridSnappedHourHeight(hourHeightValue)
                },
            )
            .verticalScroll(verticalScrollState)
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = TimetableGridVerticalPadding,
                bottom = TimetableGridVerticalPadding + navigationBarHeight,
            ),
    ) {
        TimetableGridTimeGutter(
            endMinute = endMinute,
            hourHeight = hourHeight,
            nowMinute = uiState.nowMinute,
            modifier = Modifier
                .width(TimetableGridTimeGutterWidth)
                .height(contentHeight),
        )
        TimetableGridRooms(
            uiState = uiState,
            rooms = rooms,
            contentHeight = contentHeight,
            endMinute = endMinute,
            hourHeight = hourHeight,
            horizontalScrollState = horizontalScrollState,
            onItemClick = onItemClick,
        )
    }
}

@Composable
private fun RowScope.TimetableGridRooms(
    uiState: TimetableGridSectionUiState,
    rooms: PersistentList<Room>,
    contentHeight: Dp,
    endMinute: Int,
    hourHeight: Dp,
    horizontalScrollState: ScrollState,
    onItemClick: (TimetableItemId) -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(contentHeight)
            .horizontalScroll(horizontalScrollState),
    ) {
        // The rules and the now line span the rooms rather than the viewport, so their wobble
        // holds still against the columns as the grid pans.
        Box(
            modifier = Modifier
                .width(timetableGridContentWidth(rooms.size))
                .height(contentHeight),
        ) {
            for (minute in (TimetableGridDayStartMinutes + 60)..endMinute step 60) {
                TimetableGridHourRule(
                    seed = minute,
                    modifier = Modifier.offset(
                        y = timetableGridLineOffsetY(
                            minute = minute,
                            endMinute = endMinute,
                            hourHeight = hourHeight,
                            lineHeight = TimetableGridHourRuleHeight,
                        ),
                    ),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(TimetableGridRoomColumnGap)) {
                rooms.forEach { room ->
                    TimetableGridRoomColumn(
                        room = room,
                        sessions = uiState.sessions.filter { it.room == room }.toPersistentList(),
                        hourHeight = hourHeight,
                        onItemClick = onItemClick,
                        modifier = Modifier
                            .width(TimetableGridRoomColumnWidth)
                            .height(contentHeight),
                    )
                }
            }
            uiState.nowMinute.visibleNowMinuteOrNull(endMinute)?.let { visibleNowMinute ->
                TimetableGridNowLine(
                    modifier = Modifier.offset(
                        y = timetableGridLineOffsetY(
                            minute = visibleNowMinute,
                            endMinute = endMinute,
                            hourHeight = hourHeight,
                            lineHeight = TimetableGridNowLineHeight,
                        ),
                    ),
                )
            }
        }
    }
}

@Composable
private fun TimetableGridTimeGutter(
    endMinute: Int,
    hourHeight: Dp,
    nowMinute: Int?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        for (minute in TimetableGridDayStartMinutes..endMinute step 60) {
            Box(
                modifier = Modifier
                    .height(TimetableGridHourLabelHeight)
                    .offset(
                        y = timetableGridMinuteOffsetY(minute = minute, hourHeight = hourHeight) -
                            TimetableGridHourLabelHeight / 2,
                    ),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = minute.toTimetableGridTimeLabel(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        nowMinute.visibleNowMinuteOrNull(endMinute)?.let { visibleNowMinute ->
            TimetableGridNowLabel(
                minute = visibleNowMinute,
                modifier = Modifier.offset(
                    y = timetableGridLineOffsetY(
                        minute = visibleNowMinute,
                        endMinute = endMinute,
                        hourHeight = hourHeight,
                        lineHeight = TimetableGridNowLineHeight,
                    ) - TimetableGridNowLabelHeight / 2,
                ),
            )
        }
    }
}

@Composable
private fun TimetableGridRoomColumn(
    room: Room,
    sessions: PersistentList<TimetableItem>,
    hourHeight: Dp,
    onItemClick: (TimetableItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        TimetableGridRoomHeader(room = room)
        sessions.forEach { item ->
            TimetableGridCell(
                title = item.title,
                room = item.room,
                speakers = item.speakers,
                startsAt = item.startsAt,
                endsAt = item.endsAt,
                height = timetableGridSessionHeight(
                    startsAt = item.startsAt,
                    endsAt = item.endsAt,
                    hourHeight = hourHeight,
                ),
                onItemClick = { onItemClick(item.id) },
                modifier = Modifier.offset(
                    y = timetableGridSessionOffsetY(
                        startsAt = item.startsAt,
                        hourHeight = hourHeight,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun TimetableGridHourRule(seed: Int, modifier: Modifier = Modifier) {
    SketchHorizontalDivider(
        seed = seed,
        modifier = modifier.offset(y = -TimetableGridLineWobbleInset),
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = TimetableGridHourRuleHeight,
        roughness = TimetableGridLineRoughness,
        tremor = TimetableGridLineTremor,
        tremorWavelength = TimetableGridLineTremorWavelength,
    )
}

@Composable
private fun TimetableGridNowLine(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TimetableGridNowLineHeight)
            .background(MaterialTheme.colorScheme.inverseSurface),
    )
}

@Composable
private fun BoxScope.TimetableGridNowLabel(
    minute: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .width(TimetableGridNowLabelWidth)
            .height(TimetableGridNowLabelHeight)
            .background(
                color = MaterialTheme.colorScheme.inverseSurface,
                shape = RoundedCornerShape(percent = 50),
            )
            .padding(horizontal = 4.dp),
        text = minute.toTimetableGridTimeLabel(),
        color = MaterialTheme.colorScheme.inverseOnSurface,
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Center,
        maxLines = 1,
    )
}

private val TimetableGridHourRuleHeight = 1.dp
private val TimetableGridNowLineHeight = 2.dp
private val TimetableGridHourLabelHeight = 16.dp
private val TimetableGridNowLabelWidth = 42.dp
private val TimetableGridNowLabelHeight = 18.dp
private val TimetableGridLineRoughness = 0.8.dp
private val TimetableGridLineTremor = 1.5.dp
private val TimetableGridLineTremorWavelength = 28.dp
private val TimetableGridLineWobbleInset = TimetableGridLineRoughness + TimetableGridLineTremor

/** The gesture runs between the two scales; letting go settles on the nearer one. */
private fun timetableGridSnappedHourHeight(hourHeightValue: Float): Float {
    val midpoint = (TimetableGridDefaultHourHeight.value + TimetableGridMaxHourHeight.value) / 2f
    return if (hourHeightValue < midpoint) {
        TimetableGridDefaultHourHeight.value
    } else {
        TimetableGridMaxHourHeight.value
    }
}

private fun Int?.visibleNowMinuteOrNull(endMinute: Int): Int? =
    takeIf { this != null && this in TimetableGridDayStartMinutes..endMinute }

private fun timetableGridLineOffsetY(
    minute: Int,
    endMinute: Int,
    hourHeight: Dp,
    lineHeight: Dp,
): Dp {
    val y = timetableGridMinuteOffsetY(minute = minute, hourHeight = hourHeight)
    val maxY = timetableGridContentHeight(endMinute = endMinute, hourHeight = hourHeight) - lineHeight
    return y.coerceIn(TimetableGridHeaderHeight, maxY)
}

private fun Modifier.timetableGridZoom(
    onPinchStart: () -> Unit,
    onZoom: (Float) -> Unit,
    onPinchEnd: () -> Unit,
): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        var baseline: Float? = null
        var started = false

        while (true) {
            val event = awaitPointerEvent()
            val pressedChanges = event.changes.filter { it.pressed }
            if (pressedChanges.isEmpty()) break
            if (pressedChanges.size < 2) {
                baseline = null
                continue
            }

            val verticalSpan = pressedChanges.verticalSpan()
            val currentBaseline = baseline
            if (currentBaseline == null) {
                if (verticalSpan > 24f) {
                    baseline = verticalSpan
                    if (!started) {
                        started = true
                        onPinchStart()
                    }
                }
                continue
            }

            onZoom(verticalSpan / currentBaseline)
            pressedChanges.forEach(PointerInputChange::consume)
        }
        if (started) {
            onPinchEnd()
        }
    }
}

private fun List<PointerInputChange>.verticalSpan(): Float {
    return abs(this[0].position.y - this[1].position.y)
}

private val TimetableGridDefaultRooms: PersistentList<Room> = listOf(
    Room.NARWHAL,
    Room.OTTER,
    Room.PANDA,
    Room.QUAIL,
    Room.MEERKAT,
).toPersistentList()

@LocalePreviews
@Composable
private fun TimetableGridSectionPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableGridSection(
            uiState = TimetableGridSectionUiState.fake(),
            onItemClick = {},
        )
    }
}

@LocalePreviews
@Composable
private fun TimetableGridSectionExpandedPreview(
    @PreviewParameter(KaigiSchemeProvider::class) colorScheme: KaigiColorScheme,
) {
    KaigiPreviewTheme(colorScheme) {
        TimetableGridSection(
            uiState = TimetableGridSectionUiState.fake(),
            onItemClick = {},
            initialHourHeight = TimetableGridMaxHourHeight,
        )
    }
}
