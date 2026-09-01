@file:OptIn(ExperimentalAnimationApi::class)

package io.github.droidkaigi.confsched.app

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.unveilIn
import androidx.compose.animation.veilOut
import androidx.compose.ui.graphics.Color
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent.SwipeEdge

private const val SLIDE_DURATION_MILLIS = 400

// Material 3 emphasized easing.
private val SlideEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

// The screen left behind travels this fraction of the width, so the two screens move at
// different speeds and read as a stack rather than as one sheet.
private const val PARALLAX_FRACTION = 4

private val SlideScrim = Color.Black.copy(alpha = 0.25f)

/**
 * Slides a pushed screen in from the end edge over the screen it covers.
 *
 * [SlideDirection.Start] and [SlideDirection.End] are layout-direction aware, so the push runs
 * from the right under a left-to-right layout and from the left under a right-to-left one.
 */
internal fun <T : Any> slideTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    ContentTransform(
        targetContentEnter = slideIntoContainer(
            towards = SlideDirection.Start,
            animationSpec = tween(SLIDE_DURATION_MILLIS, easing = SlideEasing),
        ),
        initialContentExit = slideOutOfContainer(
            towards = SlideDirection.Start,
            targetOffset = { it / PARALLAX_FRACTION },
            animationSpec = tween(SLIDE_DURATION_MILLIS, easing = SlideEasing),
        ) + veilOut(
            animationSpec = tween(SLIDE_DURATION_MILLIS, easing = SlideEasing),
            targetColor = SlideScrim,
        ),
    )
}

/** Reverses [slideTransitionSpec]: the popped screen leaves toward the end edge. */
internal fun <T : Any> slidePopTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    ContentTransform(
        targetContentEnter = slideIntoContainer(
            towards = SlideDirection.End,
            initialOffset = { it / PARALLAX_FRACTION },
            animationSpec = tween(SLIDE_DURATION_MILLIS, easing = SlideEasing),
        ) + unveilIn(
            animationSpec = tween(SLIDE_DURATION_MILLIS, easing = SlideEasing),
            initialColor = SlideScrim,
        ),
        initialContentExit = slideOutOfContainer(
            towards = SlideDirection.End,
            animationSpec = tween(SLIDE_DURATION_MILLIS, easing = SlideEasing),
        ),
    )
}

/**
 * [slidePopTransitionSpec] driven by a back gesture instead of by time.
 *
 * The popped screen leaves toward the end edge whichever edge the gesture came from — flipping
 * the direction with the edge would replay the push motion on a right-edge back. The framework
 * seeks this transition to the gesture's progress, so the easing stays linear: anything else
 * would bend the motion away from the finger.
 */
internal fun <T : Any> slidePredictivePopTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.(@SwipeEdge Int) -> ContentTransform = { _ ->
    val towards = SlideDirection.End
    ContentTransform(
        targetContentEnter = slideIntoContainer(
            towards = towards,
            initialOffset = { it / PARALLAX_FRACTION },
            animationSpec = tween(SLIDE_DURATION_MILLIS, easing = LinearEasing),
        ) + unveilIn(
            animationSpec = tween(SLIDE_DURATION_MILLIS, easing = LinearEasing),
            initialColor = SlideScrim,
        ),
        initialContentExit = slideOutOfContainer(
            towards = towards,
            animationSpec = tween(SLIDE_DURATION_MILLIS, easing = LinearEasing),
        ),
    )
}
