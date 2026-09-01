package io.github.droidkaigi.confsched.feature.sessions

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.NavigatorEffect
import io.github.droidkaigi.confsched.core.common.detailPane
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableItemDetailNavKey
import io.github.droidkaigi.confsched.feature.sessions.timetable.TimetableNavKey
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class NavigatorEffectTest {

    @Test
    fun skipsAPushOfTheKeyAlreadyOnTop() {
        val detail = TimetableItemDetailNavKey(TimetableItemId("1"))
        val backStack = NavBackStack<NavKey>(TimetableNavKey)

        runEffect(backStack) { navigator ->
            navigator.goTo(detail)
            navigator.goTo(detail)
        }

        assertEquals(listOf(TimetableNavKey, detail), backStack.toList())
    }

    @Test
    fun appliesAPushOfAKeyDeeperInTheStack() {
        val detail = TimetableItemDetailNavKey(TimetableItemId("1"))
        val backStack = NavBackStack<NavKey>(TimetableNavKey, detail)

        runEffect(backStack) { navigator -> navigator.goTo(TimetableNavKey) }

        assertEquals(listOf(TimetableNavKey, detail, TimetableNavKey), backStack.toList())
    }

    @Test
    fun skipsASecondPopFromAnEntryNoLongerOnTop() {
        val firstDetail = TimetableItemDetailNavKey(TimetableItemId("1"))
        val topDetail = TimetableItemDetailNavKey(TimetableItemId("2"))
        val backStack = NavBackStack<NavKey>(TimetableNavKey, firstDetail, topDetail)

        runEffect(backStack) { navigator ->
            navigator.back(origin = topDetail)
            navigator.back(origin = topDetail)
        }

        assertEquals(listOf(TimetableNavKey, firstDetail), backStack.toList())
    }

    @Test
    fun popsAnEntryBelowTheTopTogetherWithEverythingAboveIt() {
        val firstDetail = TimetableItemDetailNavKey(TimetableItemId("1"))
        val topDetail = TimetableItemDetailNavKey(TimetableItemId("2"))
        val backStack = NavBackStack<NavKey>(TimetableNavKey, firstDetail, topDetail)

        runEffect(backStack) { navigator -> navigator.back(origin = firstDetail) }

        assertEquals(listOf(TimetableNavKey), backStack.toList())
    }

    @Test
    fun keepsTheRootOnAPopFromIt() {
        val detail = TimetableItemDetailNavKey(TimetableItemId("1"))
        val backStack = NavBackStack<NavKey>(TimetableNavKey, detail)

        runEffect(backStack) { navigator -> navigator.back(origin = TimetableNavKey) }

        assertEquals(listOf(TimetableNavKey, detail), backStack.toList())
    }

    @Test
    fun replacesADetailPushedOverAnotherDetail() {
        val firstDetail = TimetableItemDetailNavKey(TimetableItemId("1"))
        val nextDetail = TimetableItemDetailNavKey(TimetableItemId("2"))
        val backStack = NavBackStack<NavKey>(TimetableNavKey, firstDetail)

        runEffect(backStack) { navigator -> navigator.goTo(nextDetail) }

        assertEquals(listOf(TimetableNavKey, nextDetail), backStack.toList())
    }

    @Test
    fun appliesRepeatedPopsWithoutAnOrigin() {
        val firstDetail = TimetableItemDetailNavKey(TimetableItemId("1"))
        val topDetail = TimetableItemDetailNavKey(TimetableItemId("2"))
        val backStack = NavBackStack<NavKey>(TimetableNavKey, firstDetail, topDetail)

        runEffect(backStack) { navigator ->
            navigator.back()
            navigator.back()
        }

        assertEquals(listOf(TimetableNavKey), backStack.toList())
    }

    @Test
    fun keepsTheRootOnAnOverPop() {
        val detail = TimetableItemDetailNavKey(TimetableItemId("1"))
        val backStack = NavBackStack<NavKey>(TimetableNavKey, detail)

        runEffect(backStack) { navigator ->
            navigator.back()
            navigator.back()
        }

        assertEquals(listOf(TimetableNavKey), backStack.toList())
    }

    @Test
    fun reselectsTheRootAlreadyOnTopWithoutReorderingTheStack() {
        val backStack = NavBackStack<NavKey>(TimetableNavKey)

        val reselections = runCapturingReselections(backStack) { navigator ->
            navigator.moveToTop(TimetableNavKey)
        }

        assertEquals(listOf(TimetableNavKey), backStack.toList())
        assertEquals(listOf(TimetableNavKey), reselections)
    }

    @Test
    fun movesADeeperRootToTopWithoutReselecting() {
        val detail = TimetableItemDetailNavKey(TimetableItemId("1"))
        val backStack = NavBackStack<NavKey>(TimetableNavKey, detail)

        val reselections = runCapturingReselections(backStack) { navigator ->
            navigator.moveToTop(TimetableNavKey)
        }

        assertEquals(listOf(detail, TimetableNavKey), backStack.toList())
        assertEquals(emptyList(), reselections)
    }

    private val entryProvider: (NavKey) -> NavEntry<NavKey> = { key ->
        NavEntry(
            key = key,
            metadata = if (key is TimetableItemDetailNavKey) detailPane() else emptyMap(),
        ) {}
    }

    private fun runEffect(backStack: NavBackStack<NavKey>, commands: (AppNavigator) -> Unit) {
        runComposeUiTest {
            val logger = SilentLogger()
            val navigator = AppNavigator(logger)
            setContent { NavigatorEffect(navigator, backStack, entryProvider, logger) }
            commands(navigator)
            waitForIdle()
        }
    }

    private fun runCapturingReselections(
        backStack: NavBackStack<NavKey>,
        commands: (AppNavigator) -> Unit,
    ): List<NavKey> {
        val captured = mutableListOf<NavKey>()
        runComposeUiTest {
            val logger = SilentLogger()
            val navigator = AppNavigator(logger)
            setContent {
                NavigatorEffect(navigator, backStack, entryProvider, logger)
                val events = remember { mutableStateListOf<NavKey>() }
                LaunchedEffect(Unit) { navigator.reselections.collect(events::add) }
                captured.clear()
                captured.addAll(events)
            }
            commands(navigator)
            waitForIdle()
        }
        return captured
    }
}

private class SilentLogger : KaigiLogger {
    override fun debug(message: () -> String) = Unit

    override fun info(message: () -> String) = Unit

    override fun warn(message: () -> String) = Unit

    override fun error(throwable: Throwable?, message: () -> String) = Unit
}
