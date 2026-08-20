package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.TimetableItemId
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Inject
@SingleIn(AppScope::class)
class SessionMemoStore {
    private val state = MutableStateFlow<PersistentMap<TimetableItemId, String>>(persistentMapOf())

    fun memos(): Flow<PersistentMap<TimetableItemId, String>> = state.asStateFlow()

    fun write(id: TimetableItemId, text: String) {
        state.update { current ->
            if (text.isEmpty()) current.remove(id) else current.put(id, text)
        }
    }

    fun clear() {
        state.value = persistentMapOf()
    }
}
