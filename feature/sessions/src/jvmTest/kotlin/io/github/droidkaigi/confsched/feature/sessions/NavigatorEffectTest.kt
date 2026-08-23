package io.github.droidkaigi.confsched.feature.sessions

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.NavigatorEffect
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
    fun skipsAPopFromAnEntryNotOnTop() {
        val firstDetail = TimetableItemDetailNavKey(TimetableItemId("1"))
        val topDetail = TimetableItemDetailNavKey(TimetableItemId("2"))
        val backStack = NavBackStack<NavKey>(TimetableNavKey, firstDetail, topDetail)

        runEffect(backStack) { navigator -> navigator.back(origin = firstDetail) }

        assertEquals(listOf(TimetableNavKey, firstDetail, topDetail), backStack.toList())
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

    private fun runEffect(backStack: NavBackStack<NavKey>, commands: (AppNavigator) -> Unit) {
        runComposeUiTest {
            val logger = SilentLogger()
            val navigator = AppNavigator(logger)
            setContent { NavigatorEffect(navigator, backStack, logger) }
            commands(navigator)
            waitForIdle()
        }
    }
}

private class SilentLogger : KaigiLogger {
    override fun debug(message: () -> String) = Unit
    override fun info(message: () -> String) = Unit
    override fun warn(message: () -> String) = Unit
    override fun error(throwable: Throwable?, message: () -> String) = Unit
}
