package io.github.droidkaigi.confsched.core.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FavoritesStorePersistenceTest {

    private val directory = Files.createTempDirectory("favorites").toFile()
    private val file = File(directory, FAVORITES_DATA_STORE_FILE_NAME)

    // DataStore refuses a second instance over a live file, and frees the one it holds when the
    // scope it was given completes rather than when the cancel is asked for.
    private suspend fun <T> TestScope.withStore(block: suspend (FavoritesStore) -> T): T {
        val job = SupervisorJob()
        val scope = CoroutineScope(job + StandardTestDispatcher(testScheduler))
        try {
            return block(FavoritesStore(PreferenceDataStoreFactory.createWithPath(scope = scope) { file.path.toPath() }))
        } finally {
            job.cancelAndJoin()
        }
    }

    @Test
    fun a_favorite_toggled_on_by_one_store_is_read_by_the_next() = runTest {
        val id = TimetableItemId("session-1")

        withStore { it.toggle(id) }

        assertTrue(file.exists(), "no preferences file was written")

        withStore { assertEquals(setOf(id), it.favoriteIds().first()) }
    }

    @Test
    fun a_favorite_toggled_twice_is_dropped_and_stays_dropped() = runTest {
        val id = TimetableItemId("session-2")

        withStore { it.toggle(id) }
        withStore { it.toggle(id) }

        withStore { assertTrue(it.favoriteIds().first().isEmpty()) }
    }

    @Test
    fun clear_removes_every_favorite() = runTest {
        withStore {
            it.toggle(TimetableItemId("session-3"))
            it.toggle(TimetableItemId("session-4"))
        }

        withStore { it.clear() }

        withStore { assertTrue(it.favoriteIds().first().isEmpty()) }
    }
}
