package io.github.droidkaigi.confsched.feature.sessions.timetable.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.foundation.lazy.layout.LazyLayoutMeasurePolicy
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.SessionRoom
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
import kotlin.math.roundToInt

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
    val scrollState = rememberTimetableGridScrollState()
    val visibleNowMinute = uiState.nowMinute.visibleNowMinuteOrNull(endMinute)
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val columnWidth = timetableGridColumnWidth(
            availableWidth = maxWidth - TimetableGridHorizontalPadding * 2 - TimetableGridTimeGutterWidth,
            roomCount = rooms.size,
        )
        val layout = remember(
            uiState.sessions,
            rooms,
            endMinute,
            hourHeight,
            visibleNowMinute,
            navigationBarHeight,
            columnWidth,
        ) {
            TimetableGridLayout(
                sessions = uiState.sessions,
                rooms = rooms,
                columnWidth = columnWidth,
                endMinute = endMinute,
                hourHeight = hourHeight,
                nowMinute = visibleNowMinute,
                bottomPadding = TimetableGridVerticalPadding + navigationBarHeight,
            )
        }
        TimetableGridContent(
            layout = layout,
            scrollState = scrollState,
            onItemClick = onItemClick,
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
    }
}

@Composable
private fun TimetableGridContent(
    layout: TimetableGridLayout,
    scrollState: TimetableGridScrollState,
    onItemClick: (TimetableItemId) -> Unit,
    onPinchStart: () -> Unit,
    onZoom: (Float) -> Unit,
    onPinchEnd: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .timetableGridZoom(
                onPinchStart = onPinchStart,
                onZoom = onZoom,
                onPinchEnd = onPinchEnd,
            )
            .timetableGridScroll(scrollState)
            .padding(start = TimetableGridHorizontalPadding),
    ) {
        TimetableGridTimeGutter(
            layout = layout,
            scrollState = scrollState,
            modifier = Modifier
                .width(TimetableGridTimeGutterWidth)
                .fillMaxHeight(),
        )
        TimetableGridRooms(
            layout = layout,
            scrollState = scrollState,
            onItemClick = onItemClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
    }
}

/**
 * Everything the rooms area draws, positioned in content coordinates.
 *
 * Positions are kept in dp so a single layout serves any density; each measure pass converts
 * only the items it places.
 */
private class TimetableGridLayout(
    val sessions: PersistentList<TimetableItem>,
    val rooms: PersistentList<SessionRoom>,
    val columnWidth: Dp,
    val endMinute: Int,
    val hourHeight: Dp,
    val nowMinute: Int?,
    val bottomPadding: Dp,
) {
    val roomsWidth: Dp = timetableGridContentWidth(roomCount = rooms.size, columnWidth = columnWidth)

    // The trailing padding scrolls with the columns so the last one is clipped by the window
    // edge rather than by an inset viewport.
    val contentWidth: Dp = roomsWidth + TimetableGridHorizontalPadding
    val contentHeight: Dp =
        TimetableGridVerticalPadding + timetableGridContentHeight(endMinute, hourHeight) + bottomPadding
    val hourMinutes: List<Int> = (TimetableGridDayStartMinutes..endMinute step 60).toList()

    val items: List<TimetableGridLayoutItem> = buildList {
        for (minute in hourMinutes.drop(1)) {
            val top = lineOffsetY(minute, TimetableGridHourRuleHeight)
            add(
                TimetableGridLayoutItem.HourRule(
                    minute = minute,
                    bounds = DpRect(0.dp, top, roomsWidth, top + TimetableGridHourRuleHeight),
                ),
            )
        }
        rooms.forEachIndexed { index, room ->
            val left = columnLeft(index)
            add(
                TimetableGridLayoutItem.RoomHeader(
                    room = room,
                    bounds = DpRect(
                        left,
                        TimetableGridVerticalPadding,
                        left + columnWidth,
                        TimetableGridVerticalPadding + TimetableGridHeaderHeight,
                    ),
                ),
            )
        }
        sessions.forEach { item ->
            val columnIndex = rooms.indexOf(item.room)
            if (columnIndex < 0) return@forEach
            val left = columnLeft(columnIndex)
            val top = TimetableGridVerticalPadding +
                timetableGridSessionOffsetY(startsAt = item.startsAt, hourHeight = hourHeight)
            val height = timetableGridSessionHeight(
                startsAt = item.startsAt,
                endsAt = item.endsAt,
                hourHeight = hourHeight,
            )
            add(
                TimetableGridLayoutItem.Session(
                    item = item,
                    bounds = DpRect(left, top, left + columnWidth, top + height),
                ),
            )
        }
        nowMinute?.let { minute ->
            val top = lineOffsetY(minute, TimetableGridNowLineHeight)
            add(
                TimetableGridLayoutItem.NowLine(
                    bounds = DpRect(0.dp, top, roomsWidth, top + TimetableGridNowLineHeight),
                ),
            )
        }
    }

    fun hourLabelTop(minute: Int): Dp =
        TimetableGridVerticalPadding +
            timetableGridMinuteOffsetY(minute = minute, hourHeight = hourHeight) -
            TimetableGridHourLabelHeight / 2

    fun nowLabelTop(minute: Int): Dp =
        lineOffsetY(minute, TimetableGridNowLineHeight) - TimetableGridNowLabelHeight / 2

    private fun columnLeft(index: Int): Dp =
        (columnWidth + TimetableGridRoomColumnGap) * index

    private fun lineOffsetY(minute: Int, lineHeight: Dp): Dp =
        TimetableGridVerticalPadding + timetableGridLineOffsetY(
            minute = minute,
            endMinute = endMinute,
            hourHeight = hourHeight,
            lineHeight = lineHeight,
        )
}

private sealed interface TimetableGridLayoutItem {
    val bounds: DpRect
    val key: Any

    class HourRule(val minute: Int, override val bounds: DpRect) : TimetableGridLayoutItem {
        override val key: Any = "hourRule:$minute"
    }

    class RoomHeader(val room: SessionRoom, override val bounds: DpRect) : TimetableGridLayoutItem {
        override val key: Any = "roomHeader:${room.name}"
    }

    class Session(val item: TimetableItem, override val bounds: DpRect) : TimetableGridLayoutItem {
        override val key: Any = "session:${item.id.value}"
    }

    class NowLine(override val bounds: DpRect) : TimetableGridLayoutItem {
        override val key: Any = "nowLine"
    }
}

private fun Density.toIntRect(bounds: DpRect): IntRect = IntRect(
    left = bounds.left.roundToPx(),
    top = bounds.top.roundToPx(),
    right = bounds.right.roundToPx(),
    bottom = bounds.bottom.roundToPx(),
)

/**
 * Lays out only the items that intersect the viewport; the hour rules wobble past their measured
 * height, so the viewport is padded before the intersection test.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimetableGridRooms(
    layout: TimetableGridLayout,
    scrollState: TimetableGridScrollState,
    onItemClick: (TimetableItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemProvider = remember(layout, onItemClick) {
        object : LazyLayoutItemProvider {
            override val itemCount: Int get() = layout.items.size

            override fun getKey(index: Int): Any = layout.items[index].key

            @Composable
            override fun Item(index: Int, key: Any) {
                TimetableGridLayoutItemContent(item = layout.items[index], onItemClick = onItemClick)
            }
        }
    }
    val measurePolicy = remember(layout, scrollState) {
        LazyLayoutMeasurePolicy { constraints ->
            val viewportWidth = constraints.maxWidth
            val viewportHeight = constraints.maxHeight
            scrollState.updateBounds(
                maxScrollX = (layout.contentWidth.toPx() - viewportWidth).coerceAtLeast(0f),
                maxScrollY = (layout.contentHeight.toPx() - viewportHeight).coerceAtLeast(0f),
            )
            val scrollX = scrollState.scrollX.roundToInt()
            val scrollY = scrollState.scrollY.roundToInt()
            val slack = TimetableGridLineWobbleInset.roundToPx()
            val viewport = IntRect(
                left = scrollX - slack,
                top = scrollY - slack,
                right = scrollX + viewportWidth + slack,
                bottom = scrollY + viewportHeight + slack,
            )
            val placements = buildList {
                layout.items.forEachIndexed { index, item ->
                    val bounds = toIntRect(item.bounds)
                    if (!bounds.overlaps(viewport)) return@forEachIndexed
                    val placeables = measure(index, Constraints.fixed(bounds.width, bounds.height))
                    add(Triple(placeables, bounds.left - scrollX, bounds.top - scrollY))
                }
            }
            layout(viewportWidth, viewportHeight) {
                placements.forEach { (placeables, x, y) ->
                    placeables.forEach { placeable: Placeable -> placeable.place(x, y) }
                }
            }
        }
    }
    LazyLayout(
        itemProvider = { itemProvider },
        modifier = modifier.clipToBounds(),
        measurePolicy = measurePolicy,
    )
}

@Composable
private fun TimetableGridLayoutItemContent(
    item: TimetableGridLayoutItem,
    onItemClick: (TimetableItemId) -> Unit,
) {
    when (item) {
        is TimetableGridLayoutItem.HourRule -> TimetableGridHourRule(seed = item.minute)

        is TimetableGridLayoutItem.RoomHeader -> TimetableGridRoomHeader(room = item.room)

        is TimetableGridLayoutItem.Session -> TimetableGridCell(
            title = item.item.title,
            room = item.item.room,
            speakers = item.item.speakers,
            startsAt = item.item.startsAt,
            endsAt = item.item.endsAt,
            height = item.bounds.bottom - item.bounds.top,
            onItemClick = { onItemClick(item.item.id) },
        )

        is TimetableGridLayoutItem.NowLine -> TimetableGridNowLine()
    }
}

@Composable
private fun TimetableGridTimeGutter(
    layout: TimetableGridLayout,
    scrollState: TimetableGridScrollState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.clipToBounds()) {
        val scrollY = with(LocalDensity.current) { scrollState.scrollY.toDp() }
        for (minute in layout.hourMinutes) {
            Box(
                modifier = Modifier
                    .height(TimetableGridHourLabelHeight)
                    .offset(y = layout.hourLabelTop(minute) - scrollY),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = minute.toTimetableGridTimeLabel(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        layout.nowMinute?.let { minute ->
            TimetableGridNowLabel(
                minute = minute,
                modifier = Modifier.offset(y = layout.nowLabelTop(minute) - scrollY),
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

private val TimetableGridHorizontalPadding = 16.dp
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

private val TimetableGridDefaultRooms: PersistentList<SessionRoom> = listOf(
    SessionRoom.NARWHAL,
    SessionRoom.OTTER,
    SessionRoom.PANDA,
    SessionRoom.QUAIL,
    SessionRoom.MEERKAT,
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
