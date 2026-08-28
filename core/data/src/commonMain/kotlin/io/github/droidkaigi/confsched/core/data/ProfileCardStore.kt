package io.github.droidkaigi.confsched.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.Mascot
import io.github.droidkaigi.confsched.core.model.ProfileCard
import io.github.droidkaigi.confsched.core.model.Sketchiness
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** The stored form of a [ProfileCard]: the avatar itself lives in [AvatarImageStore], only its path here. */
internal data class StoredProfileCard(
    val nickName: String,
    val occupation: String,
    val link: String,
    val mascot: Mascot,
    val sketchiness: Sketchiness,
    val avatarImagePath: String?,
)

@Inject
@SingleIn(AppScope::class)
class ProfileCardStore(@ProfileCardDataStoreQualifier private val dataStore: DataStore<Preferences>) {

    internal fun card(): Flow<StoredProfileCard?> = dataStore.data.map { it.readCard() }

    suspend fun save(card: ProfileCard, avatarImagePath: String?) {
        dataStore.edit { preferences ->
            preferences[NICK_NAME_KEY] = card.nickName
            preferences[OCCUPATION_KEY] = card.occupation
            preferences[LINK_KEY] = card.link
            preferences[MASCOT_KEY] = card.mascot.name
            preferences[SKETCHINESS_KEY] = card.sketchiness.name
            if (avatarImagePath == null) {
                preferences.remove(AVATAR_IMAGE_PATH_KEY)
            } else {
                preferences[AVATAR_IMAGE_PATH_KEY] = avatarImagePath
            }
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    // The nickname is written by every save, so its absence is what "no card yet" looks like.
    private fun Preferences.readCard(): StoredProfileCard? {
        val nickName = this[NICK_NAME_KEY] ?: return null
        return StoredProfileCard(
            nickName = nickName,
            occupation = this[OCCUPATION_KEY].orEmpty(),
            link = this[LINK_KEY].orEmpty(),
            mascot = this[MASCOT_KEY]
                ?.let { name -> Mascot.entries.firstOrNull { it.name == name } }
                ?: ProfileCard.DefaultMascot,
            sketchiness = this[SKETCHINESS_KEY]
                ?.let { name -> Sketchiness.entries.firstOrNull { it.name == name } }
                ?: ProfileCard.DefaultSketchiness,
            avatarImagePath = this[AVATAR_IMAGE_PATH_KEY],
        )
    }

    private companion object {
        val NICK_NAME_KEY = stringPreferencesKey("profileCard.nickName")
        val OCCUPATION_KEY = stringPreferencesKey("profileCard.occupation")
        val LINK_KEY = stringPreferencesKey("profileCard.link")
        val MASCOT_KEY = stringPreferencesKey("profileCard.mascot")
        val SKETCHINESS_KEY = stringPreferencesKey("profileCard.sketchiness")
        val AVATAR_IMAGE_PATH_KEY = stringPreferencesKey("profileCard.avatarImagePath")
    }
}
