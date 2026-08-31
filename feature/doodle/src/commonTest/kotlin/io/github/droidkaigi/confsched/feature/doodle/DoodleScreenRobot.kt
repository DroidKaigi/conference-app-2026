package io.github.droidkaigi.confsched.feature.doodle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.zacsweers.metro.createGraphFactory
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleEdit
import io.github.droidkaigi.confsched.core.model.DoodlePoint
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.testing.Robot
import io.github.droidkaigi.confsched.core.ui.DOODLE_CANVAS_FRAME_TEST_TAG
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DoodleScreenRobot(composeUiTest: ComposeUiTest, private val target: DoodleTarget) : Robot(composeUiTest) {

    private val graph = createGraphFactory<DoodleScreenTestGraph.Factory>().create(target)
    private var backCount = 0

    fun setupSavedDoodle(doodle: Doodle) {
        graph.doodlesSubscriptionKey.set(persistentMapOf(target to doodle))
    }

    fun setupNoSavedDoodles() {
        graph.doodlesSubscriptionKey.set(persistentMapOf())
    }

    fun setupCard(card: ProfileCard?) {
        graph.profileCardSubscriptionKey.set(card)
    }

    fun setupFailingSave() {
        graph.doodleMutationKey.failWith(IllegalStateException("boom"))
    }

    fun setupContent() {
        setScreenContent {
            context(graph.screenContext) {
                DoodleScreenRoot(onNavigateBack = { backCount++ })
            }
        }
    }

    /** Composes the screen as a window wide enough for the layout to lay both card faces out. */
    fun setupExpandedWindowContent() {
        setScreenContent {
            WideWindowLayout {
                context(graph.screenContext) {
                    DoodleScreenRoot(onNavigateBack = { backCount++ })
                }
            }
        }
    }

    /**
     * Lays [content] out as though the test host were [ExpandedWindowWidth] wide. The host's size is
     * fixed, so the density is scaled instead of the surface, which keeps a gesture's pixels landing
     * where the layout put the control.
     */
    @Composable
    private fun WideWindowLayout(content: @Composable () -> Unit) {
        BoxWithConstraints {
            val scaledDensity = Density(density = constraints.maxWidth / ExpandedWindowWidth.value)
            CompositionLocalProvider(LocalDensity provides scaledDensity) {
                Box(modifier = Modifier.fillMaxSize()) { content() }
            }
        }
    }

    /**
     * Draws a horizontal stroke through the centre of the [canvasIndex]th canvas, reaching
     * [STROKE_HALF_SPAN_FRACTION] of the frame's width either side of that centre.
     */
    fun drawStroke(canvasIndex: Int) {
        canvas(canvasIndex).performTouchInput {
            down(Offset(centerX - width * STROKE_HALF_SPAN_FRACTION, centerY))
            moveTo(Offset(centerX, centerY))
            moveTo(Offset(centerX + width * STROKE_HALF_SPAN_FRACTION, centerY))
            up()
        }
        composeUiTest.waitForIdle()
    }

    /** Presses the centre of the [canvasIndex]th canvas and lifts again without travelling. */
    fun tapCanvas(canvasIndex: Int) {
        canvas(canvasIndex).performTouchInput {
            down(Offset(centerX, centerY))
            up()
        }
        composeUiTest.waitForIdle()
    }

    /** Spreads two fingers about the canvas centre, which magnifies by [PINCH_ZOOM_FACTOR]. */
    fun pinchOpen(canvasIndex: Int) {
        canvas(canvasIndex).performTouchInput {
            val span = width * PINCH_HALF_SPAN_FRACTION
            down(pointerId = 0, position = Offset(centerX - span, centerY))
            down(pointerId = 1, position = Offset(centerX + span, centerY))
            updatePointerTo(pointerId = 0, position = Offset(centerX - span * PINCH_ZOOM_FACTOR, centerY))
            updatePointerTo(pointerId = 1, position = Offset(centerX + span * PINCH_ZOOM_FACTOR, centerY))
            move()
            up(pointerId = 0)
            up(pointerId = 1)
        }
        composeUiTest.waitForIdle()
    }

    fun clickButton(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).performClick()
        composeUiTest.waitForIdle()
    }

    fun clickZoomIn() {
        composeUiTest.onNodeWithContentDescription("Zoom in").performClick()
        composeUiTest.waitForIdle()
    }

    fun clickBack() {
        composeUiTest.onNodeWithContentDescription("Back").performClick()
        composeUiTest.waitForIdle()
    }

    fun checkButtonDisplayed(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertIsDisplayed()
    }

    fun checkButtonEnabled(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertIsEnabled()
    }

    fun checkButtonDisabled(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertIsNotEnabled()
    }

    fun checkCanvasCount(count: Int) {
        assertEquals(count, composeUiTest.onAllNodesWithTag(DOODLE_CANVAS_FRAME_TEST_TAG).fetchSemanticsNodes().size)
    }

    fun checkSavedDoodles(vararg edits: DoodleEdit) {
        assertEquals(edits.toList(), graph.doodleMutationKey.invocations.tryReceive().getOrThrow())
    }

    /**
     * Asserts that the stroke saved for [target] is the one [drawStroke] traced, mapped back into
     * the About hero's own dp space at [zoom]: its y sits halfway down that space, and its ends
     * reach the fraction of the reference width the gesture covered, divided by the magnification.
     */
    fun checkWallStrokeDrawnAcrossTheCenter(zoom: Float) {
        val edits = graph.doodleMutationKey.invocations.tryReceive().getOrThrow()
        val stroke = edits.single { it.target == target }.doodle.strokes.single()
        val reference = DoodleTarget.AboutWall.referenceSize
        val halfSpan = reference.width.value * STROKE_HALF_SPAN_FRACTION / zoom
        val centerY = reference.height.value / 2f
        assertPointClose(DoodlePoint(x = -halfSpan, y = centerY), stroke.points.first())
        assertPointClose(DoodlePoint(x = 0f, y = centerY), stroke.points[stroke.points.size / 2])
        assertPointClose(DoodlePoint(x = halfSpan, y = centerY), stroke.points.last())
    }

    /**
     * Asserts that the stroke saved for [target] is the single point [tapCanvas] pressed, mapped
     * back into the About hero's own dp space: the middle of that space's top edge, halfway down.
     */
    fun checkWallDotDrawnAtTheCenter() {
        val edits = graph.doodleMutationKey.invocations.tryReceive().getOrThrow()
        val stroke = edits.single { it.target == target }.doodle.strokes.single()
        val reference = DoodleTarget.AboutWall.referenceSize
        assertPointClose(DoodlePoint(x = 0f, y = reference.height.value / 2f), stroke.points.single())
    }

    /** Asserts the single stroke saved for [strokeTarget] was laid down [width] dp wide. */
    fun checkSavedStrokeWidth(strokeTarget: DoodleTarget, width: Float) {
        val edits = graph.doodleMutationKey.invocations.tryReceive().getOrThrow()
        assertEquals(width, edits.single { it.target == strokeTarget }.doodle.strokes.single().width)
    }

    /** Asserts one save carried both faces, each with the given number of strokes. */
    fun checkSavedFaceStrokeCounts(front: Int, back: Int) {
        val edits = graph.doodleMutationKey.invocations.tryReceive().getOrThrow()
        assertEquals(front, edits.single { it.target == DoodleTarget.ProfileCardFront }.doodle.strokes.size)
        assertEquals(back, edits.single { it.target == DoodleTarget.ProfileCardBack }.doodle.strokes.size)
    }

    fun checkTextAbsent(label: StringResource) {
        composeUiTest.onNodeWithText(text(label)).assertDoesNotExist()
    }

    fun checkNothingSaved() {
        assertTrue(graph.doodleMutationKey.invocations.tryReceive().isFailure, "a save reached the data layer")
    }

    fun checkBackInvoked(times: Int) {
        assertEquals(times, backCount)
    }

    private fun canvas(index: Int): SemanticsNodeInteraction =
        composeUiTest.onAllNodesWithTag(DOODLE_CANVAS_FRAME_TEST_TAG)[index]

    private fun text(label: StringResource): String = runBlocking { getString(label) }

    private fun assertPointClose(expected: DoodlePoint, actual: DoodlePoint) {
        assertTrue(
            abs(expected.x - actual.x) <= POINT_TOLERANCE && abs(expected.y - actual.y) <= POINT_TOLERANCE,
            "expected $expected, was $actual",
        )
    }
}

private val ExpandedWindowWidth = 1000.dp

private const val STROKE_HALF_SPAN_FRACTION = 0.2f
private const val PINCH_HALF_SPAN_FRACTION = 0.1f
private const val PINCH_ZOOM_FACTOR = 3f

// A gesture position is injected in whole pixels, so a coordinate mapped back into dp lands within
// a dp of the exact figure.
private const val POINT_TOLERANCE = 1.5f
