package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.uuid.Uuid

/**
 * Holds the profile card's avatar bytes and hands back the path they were written to. On wasmJs
 * that path is an IndexedDB key rather than a filesystem path.
 */
@Inject
@SingleIn(AppScope::class)
class AvatarImageStore(private val fileStorage: FileStorage) {
    // The path must differ per save: the stored card re-emits only when the path it records changes.
    suspend fun save(bytes: ByteArray): String {
        val path = "$AVATAR_IMAGE_DIRECTORY/${Uuid.random()}"
        fileStorage.put(path, bytes)
        return path
    }

    suspend fun load(path: String): ByteArray? = fileStorage.get(path)

    suspend fun delete(path: String) = fileStorage.delete(path)
}

private const val AVATAR_IMAGE_DIRECTORY = "profile"
