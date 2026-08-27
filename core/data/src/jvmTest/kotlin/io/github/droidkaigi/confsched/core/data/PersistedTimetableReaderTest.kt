package io.github.droidkaigi.confsched.core.data

import io.github.droidkaigi.confsched.core.model.Timetable
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PersistedTimetableReaderTest {

    private val fileStorage = InMemoryFileStorage()
    private val serverEnvironmentStore = ServerEnvironmentStore()
    private val scopedFileStorage = ServerEnvironmentScopedFileStorage(fileStorage, serverEnvironmentStore)
    private val reader = PersistedTimetableReader(scopedFileStorage)

    @Test
    fun the_stored_timetable_is_the_first_emission() = runTest {
        persist("session-1")

        val collected = collectTimetables()

        assertEquals(listOf("session-1"), collected.single()?.items?.map { it.id.value })
    }

    @Test
    fun a_timetable_persisted_after_collection_started_is_emitted() = runTest {
        val collected = collectTimetables()

        assertNull(collected.single())

        persist("session-1")

        assertEquals(2, collected.size)
        assertEquals(listOf("session-1"), collected.last()?.items?.map { it.id.value })
    }

    @Test
    fun persisting_the_same_timetable_again_is_not_emitted() = runTest {
        persist("session-1")
        val collected = collectTimetables()

        persist("session-1")

        assertEquals(1, collected.size)
    }

    private fun TestScope.collectTimetables(): List<Timetable?> {
        val collected = mutableListOf<Timetable?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            reader.timetables().toList(collected)
        }
        return collected
    }

    private suspend fun persist(vararg sessionIds: String) {
        val response = TimetableResponse(
            status = HttpStatusResponse.OK,
            sessions = sessionIds.map { id ->
                SessionResponse(
                    id = id,
                    title = LocaledResponse(ja = "Session $id", en = "Session $id"),
                    speakers = emptyList(),
                    startsAt = "2026-09-02T10:00:00+09:00",
                    endsAt = "2026-09-02T10:40:00+09:00",
                    language = LanguageResponse.JAPANESE,
                    roomId = 1L,
                    lengthInMinutes = 40,
                    sessionType = SessionTypeResponse.NORMAL,
                    noShow = false,
                    message = null,
                    targetAudience = LocaledResponse(ja = "All", en = "All"),
                    interpretationTarget = false,
                    asset = SessionAssetResponse(),
                    sessionCategoryItemId = null,
                )
            },
            rooms = listOf(RoomResponse(name = LocaledResponse(ja = "Room 1", en = "Room 1"), id = 1L, sort = 0)),
            speakers = emptyList(),
            categories = emptyList(),
        )
        scopedFileStorage.put(
            TIMETABLE_PERSIST_KEY,
            persistedQueryJson.encodeToString(response).encodeToByteArray(),
        )
        reader.notifyPersisted()
    }
}

private class InMemoryFileStorage : FileStorage {
    private val entries = mutableMapOf<String, ByteArray>()

    override suspend fun get(key: String): ByteArray? = entries[key]

    override suspend fun put(key: String, bytes: ByteArray) {
        entries[key] = bytes
    }

    override suspend fun delete(key: String) {
        entries.remove(key)
    }

    override suspend fun clear() = entries.clear()
}
