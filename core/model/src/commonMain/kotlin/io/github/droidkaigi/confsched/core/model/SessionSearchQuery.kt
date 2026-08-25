package io.github.droidkaigi.confsched.core.model

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

data class SessionSearchQuery(
    val text: String = "",
    val day: DroidKaigi2026Day? = null,
    val categoryIds: PersistentSet<Long> = persistentSetOf(),
    val sessionTypes: PersistentSet<SessionType> = persistentSetOf(),
    val languages: PersistentSet<Language> = persistentSetOf(),
) {
    val normalizedText = text.trim()

    val hasActiveFilters: Boolean
        get() = day != null ||
            categoryIds.isNotEmpty() ||
            sessionTypes.isNotEmpty() ||
            languages.isNotEmpty()

    val isEmpty: Boolean
        get() = normalizedText.isEmpty() && !hasActiveFilters

    fun matches(item: TimetableItem): Boolean = matchesText(item) &&
        (day == null || item.day == day) &&
        (categoryIds.isEmpty() || item.category?.id in categoryIds) &&
        (sessionTypes.isEmpty() || item.sessionType in sessionTypes) &&
        (languages.isEmpty() || item.language in languages)

    fun toggleDay(day: DroidKaigi2026Day): SessionSearchQuery =
        copy(day = if (day == this.day) null else day)

    fun toggleCategory(categoryId: Long): SessionSearchQuery =
        copy(categoryIds = categoryIds.toggle(categoryId))

    fun toggleSessionType(sessionType: SessionType): SessionSearchQuery =
        copy(sessionTypes = sessionTypes.toggle(sessionType))

    fun toggleLanguage(language: Language): SessionSearchQuery = copy(languages = languages.toggle(language))

    fun clearFilters(): SessionSearchQuery = SessionSearchQuery(text = text)

    private fun matchesText(item: TimetableItem): Boolean = normalizedText.isEmpty() ||
        item.title.ja.contains(normalizedText, ignoreCase = true) ||
        item.title.en.contains(normalizedText, ignoreCase = true) ||
        item.speakers.any { speaker -> speaker.name.contains(normalizedText, ignoreCase = true) } ||
        item.room.name.contains(normalizedText, ignoreCase = true)
}

private fun <T> PersistentSet<T>.toggle(value: T): PersistentSet<T> =
    if (value in this) removing(value) else adding(value)
