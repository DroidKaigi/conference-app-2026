package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.Timetable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.decodeFromString

internal const val TIMETABLE_PERSIST_KEY = "timetable"

/**
 * Reads the timetable payload the timetable query persisted, for callers outside the Soil
 * runtime such as home-screen widgets.
 */
@Inject
@SingleIn(AppScope::class)
class PersistedTimetableReader(private val fileStorage: ServerEnvironmentScopedFileStorage) {
    /** Emits whenever a fetch replaced the persisted payload. */
    val updates: Flow<Unit>
        field = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /** The persisted timetable, or null when nothing decodable is stored yet. */
    suspend fun read(): Timetable? = runCatching {
        fileStorage.get(TIMETABLE_PERSIST_KEY)
            ?.decodeToString()
            ?.let { persistedQueryJson.decodeFromString<TimetableResponse>(it) }
            ?.toTimetable()
    }.getOrNull()

    internal fun notifyPersisted() {
        updates.tryEmit(Unit)
    }
}
