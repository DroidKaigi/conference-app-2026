package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiClock

/**
 * Holds the profile card's avatar bytes and hands back the path they were written to. On wasmJs
 * that path is an IndexedDB key rather than a filesystem path.
 */
@Inject
@SingleIn(AppScope::class)
class AvatarImageStore(
    private val fileStorage: FileStorage,
    private val clock: KaigiClock,
) {
    // A fresh path per save: the stored record must differ when only the image changes, or the
    // subscription's distinctUntilChanged keeps showing the previous avatar.
    suspend fun save(bytes: ByteArray): String {
        val path = "profile/avatar-${clock.now().toEpochMilliseconds()}"
        fileStorage.put(path, bytes)
        return path
    }

    suspend fun load(path: String): ByteArray? = fileStorage.get(path)

    suspend fun delete(path: String) = fileStorage.delete(path)
}
