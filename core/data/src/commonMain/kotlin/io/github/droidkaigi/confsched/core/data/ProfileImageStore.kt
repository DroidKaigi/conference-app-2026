package io.github.droidkaigi.confsched.core.data

import dev.zacsweers.metro.Inject

@Inject
class ProfileImageStore(private val store: FileStorage) {
    suspend fun loadImage(profileId: String): ByteArray? = store.get(keyOf(profileId))

    suspend fun saveImage(profileId: String, bytes: ByteArray) = store.put(keyOf(profileId), bytes)

    suspend fun deleteImage(profileId: String) = store.delete(keyOf(profileId))

    private fun keyOf(profileId: String): String = "profile.image.$profileId"
}
