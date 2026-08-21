package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

/**
 * What the search screen is looking for: a word to match, and the filters narrowing it.
 *
 * An empty filter means every value passes it, so picking none and picking all read the same.
 */
data class SessionSearchQuery(
    val text: String = "",
    val day: DroidKaigi2026Day? = null,
    val categoryIds: PersistentSet<Long> = persistentSetOf(),
    val sessionTypes: PersistentSet<SessionType> = persistentSetOf(),
    val languages: PersistentSet<Language> = persistentSetOf(),
) {

    /** Nothing to search on, so the screen opens rather than reporting an empty result. */
    val isEmpty: Boolean
        get() = text.isBlank() &&
            day == null &&
            categoryIds.isEmpty() &&
            sessionTypes.isEmpty() &&
            languages.isEmpty()

    /** Whether [item] answers this query. */
    fun matches(item: TimetableItem): Boolean = matchesText(item) &&
        (day == null || item.day == day) &&
        (categoryIds.isEmpty() || item.category?.id in categoryIds) &&
        (sessionTypes.isEmpty() || item.sessionType in sessionTypes) &&
        (languages.isEmpty() || item.language in languages)

    fun toggleCategory(id: Long): SessionSearchQuery = copy(categoryIds = categoryIds.toggle(id))

    fun toggleSessionType(sessionType: SessionType): SessionSearchQuery =
        copy(sessionTypes = sessionTypes.toggle(sessionType))

    fun toggleLanguage(language: Language): SessionSearchQuery = copy(languages = languages.toggle(language))

    /**
     * Whether [item] answers the word typed, across its title, its speaker and its room.
     *
     * Both sides of a title are matched, so a word typed in either language finds the session
     * whichever language the app is running in.
     */
    private fun matchesText(item: TimetableItem): Boolean = text.isBlank() ||
        item.title.ja.contains(text, ignoreCase = true) ||
        item.title.en.contains(text, ignoreCase = true) ||
        item.speaker.contains(text, ignoreCase = true) ||
        item.room.name.contains(text, ignoreCase = true)
}

private fun <T> PersistentSet<T>.toggle(value: T): PersistentSet<T> =
    if (value in this) remove(value) else add(value)
