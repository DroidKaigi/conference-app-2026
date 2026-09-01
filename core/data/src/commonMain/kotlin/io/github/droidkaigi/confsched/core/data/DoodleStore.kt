package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.Doodle
import io.github.droidkaigi.confsched.core.model.DoodleTarget
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
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
    // observing the doodles sees a save as soon as it is written.
    private val cached = MutableStateFlow<PersistentMap<DoodleTarget, Doodle>?>(null)
    private val seeding = Mutex()

    fun doodles(): Flow<PersistentMap<DoodleTarget, Doodle>> = flow {
        current()
        emitAll(cached.filterNotNull())
    }

    suspend fun save(target: DoodleTarget, doodle: Doodle) {
        val doodles = current()
        if (doodle.strokes.isEmpty()) {
            fileStorage.delete(target.fileKey)
            cached.value = doodles.remove(target)
        } else {
            fileStorage.put(target.fileKey, persistedQueryJson.encodeToString(doodle).encodeToByteArray())
            cached.value = doodles.put(target, doodle)
        }
    }

    suspend fun clear() {
        DoodleTarget.entries.forEach { fileStorage.delete(it.fileKey) }
        cached.value = persistentMapOf()
    }

    private suspend fun current(): PersistentMap<DoodleTarget, Doodle> = seeding.withLock {
        cached.value ?: read().also { cached.value = it }
    }

    private suspend fun read(): PersistentMap<DoodleTarget, Doodle> = DoodleTarget.entries
        .mapNotNull { target -> read(target)?.let { target to it } }
        .toMap()
        .toPersistentMap()

    // A payload that no longer decodes is treated as no doodle rather than failing the read.
    private suspend fun read(target: DoodleTarget): Doodle? {
        val json = fileStorage.get(target.fileKey)?.decodeToString() ?: return null
        return try {
            persistedQueryJson.decodeFromString<Doodle>(json)
        } catch (e: SerializationException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}

private val DoodleTarget.fileKey: String get() = "doodle/${name.lowercase()}"
