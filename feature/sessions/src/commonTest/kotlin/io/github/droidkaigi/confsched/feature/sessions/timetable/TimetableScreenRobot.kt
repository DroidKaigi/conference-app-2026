package io.github.droidkaigi.confsched.feature.sessions.timetable

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.droidkaigi.confsched.core.common.LocalSafeClickInvoker
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.common.SafeClickInvoker
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.model.DroidKaigi2026Day
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableIdsSubscriptionKey
import io.github.droidkaigi.confsched.core.model.FavoriteTimetableItemIdMutationKey
import io.github.droidkaigi.confsched.core.model.Timetable
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import io.github.droidkaigi.confsched.core.model.TimetableQueryKey
import io.github.droidkaigi.confsched.core.testing.Robot
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.flowOf
import soil.query.MutationId
import soil.query.QueryId
import soil.query.SubscriptionId
import soil.query.SwrCachePlus
import soil.query.buildMutationKey
import soil.query.buildQueryKey
import soil.query.buildSubscriptionKey
import soil.query.compose.SwrClientProvider
import kotlin.time.Duration

@OptIn(ExperimentalTestApi::class)
class TimetableScreenRobot(composeUiTest: ComposeUiTest) : Robot(composeUiTest) {

    fun setupContent(
        timetable: Timetable,
        favoriteIds: PersistentSet<TimetableItemId> = persistentSetOf(),
    ) {
        val queryKey: TimetableQueryKey = buildQueryKey(
            id = QueryId("robot-timetable"),
            fetch = { timetable },
        )
        val subscriptionKey: FavoriteTimetableIdsSubscriptionKey = buildSubscriptionKey(
            id = SubscriptionId("robot-favoriteIds"),
            subscribe = { flowOf(favoriteIds) },
        )
        val mutationKey: FavoriteTimetableItemIdMutationKey = buildMutationKey(
            id = MutationId("robot-favorite"),
            mutate = { },
        )
        val screenContext = TimetableScreenContext(
            timetableQueryKey = queryKey,
            favoriteTimetableIdsSubscriptionKey = subscriptionKey,
            presenterContext = TimetablePresenterContext(favoriteTimetableItemIdMutationKey = mutationKey),
        )
        val client = SwrCachePlus(CoroutineScope(SupervisorJob() + Dispatchers.Unconfined))

        composeUiTest.setContent {
            SwrClientProvider(client = client) {
                CompositionLocalProvider(
                    LocalSnackbarHostState provides SnackbarHostState(),
                    LocalSafeClickInvoker provides SafeClickInvoker(interval = Duration.ZERO),
                ) {
                    context(screenContext) {
                        TimetableScreenRoot(onNavigateToDetail = {})
                    }
                }
            }
        }
        composeUiTest.waitForIdle()
    }

    fun clickDayTab(day: DroidKaigi2026Day) {
        composeUiTest.onNodeWithText(day.name).performClick()
        composeUiTest.waitForIdle()
    }

    fun checkSessionDisplayed(title: String) {
        composeUiTest.onNodeWithText(title).assertIsDisplayed()
    }

    fun checkSessionDoesNotExist(title: String) {
        composeUiTest.onNodeWithText(title).assertDoesNotExist()
    }

    fun clickRawResponseHeader() {
        composeUiTest.onNodeWithText("Raw response").performClick()
        composeUiTest.waitForIdle()
    }

    fun checkRawResponseDisplayed(rawResponse: String) {
        composeUiTest.onNodeWithText(rawResponse).assertIsDisplayed()
    }

    fun checkRawResponseDoesNotExist(rawResponse: String) {
        composeUiTest.onNodeWithText(rawResponse).assertDoesNotExist()
    }
}
