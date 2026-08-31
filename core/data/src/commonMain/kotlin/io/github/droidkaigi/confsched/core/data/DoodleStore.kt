package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.Doodle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString

@Inject
@SingleIn(AppScope::class)
class DoodleStore(private val fileStorage: FileStorage) {

    // The stored bytes are read once and every later value comes from a save, so a screen
    // observing the doodle sees a save as soon as it is written.
    private val cached = MutableStateFlow<Doodle?>(null)
    private val seeding = Mutex()

    fun doodle(): Flow<Doodle> = flow {
        seeding.withLock {
            if (cached.value == null) cached.value = read()
        }
        emitAll(cached.filterNotNull())
    }

    suspend fun save(doodle: Doodle) {
        if (doodle.strokes.isEmpty()) {
            fileStorage.delete(DOODLE_KEY)
        } else {
            fileStorage.put(DOODLE_KEY, persistedQueryJson.encodeToString(doodle).encodeToByteArray())
        }
        cached.value = doodle
    }

    suspend fun clear() {
        fileStorage.delete(DOODLE_KEY)
        cached.value = Doodle.Empty
    }

    // A payload that no longer decodes is treated as no doodle rather than failing the read.
    private suspend fun read(): Doodle {
        val json = fileStorage.get(DOODLE_KEY)?.decodeToString() ?: return Doodle.Empty
        return try {
            persistedQueryJson.decodeFromString<Doodle>(json)
        } catch (e: SerializationException) {
            Doodle.Empty
        } catch (e: IllegalArgumentException) {
            Doodle.Empty
        }
    }
}

private const val DOODLE_KEY = "doodle/about"
