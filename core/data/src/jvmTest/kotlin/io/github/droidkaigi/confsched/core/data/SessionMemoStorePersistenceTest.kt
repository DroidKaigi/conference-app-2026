package io.github.droidkaigi.confsched.core.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionMemoStorePersistenceTest {

    private val directory = Files.createTempDirectory("session-memo").toFile()
    private val file = File(directory, SESSION_MEMO_DATA_STORE_FILE_NAME)

    // DataStore refuses a second instance over a live file, so each store owns a scope the test cancels.
    private inline fun <T> withStore(block: (SessionMemoStore) -> T): T {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            return block(SessionMemoStore(PreferenceDataStoreFactory.createWithPath(scope = scope) { file.path.toPath() }))
        } finally {
            scope.cancel()
        }
    }

    @Test
    fun a_memo_written_by_one_store_is_read_by_the_next() = runBlocking {
        val id = TimetableItemId("session-1")

        withStore { it.write(id, "a note") }

        assertTrue(file.exists(), "no preferences file was written")
        assertTrue(file.readBytes().decodeToString().contains("a note"), "the memo is not in the file")

        withStore { assertEquals("a note", it.memos().first()[id]) }
    }

    @Test
    fun an_emptied_memo_is_dropped_and_stays_dropped() = runBlocking {
        val id = TimetableItemId("session-2")

        withStore { it.write(id, "a note") }
        withStore { it.write(id, "") }

        withStore { assertNull(it.memos().first()[id]) }
    }

    @Test
    fun clear_removes_every_memo() = runBlocking {
        withStore {
            it.write(TimetableItemId("session-3"), "one")
            it.write(TimetableItemId("session-4"), "two")
        }

        withStore { it.clear() }

        withStore { assertTrue(it.memos().first().isEmpty()) }
    }
}
