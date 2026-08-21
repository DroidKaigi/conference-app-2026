package io.github.droidkaigi.confsched.core.model

/** What the search screen is looking for. */
data class SessionSearchQuery(val text: String = "") {

    /** Nothing to search on, so the screen opens rather than reporting an empty result. */
    val isEmpty: Boolean get() = text.isBlank()

    /**
     * Whether [item] answers this query.
     *
     * Both sides of a title are matched, so a word typed in either language finds the session
     * whichever language the app is running in.
     */
    fun matches(item: TimetableItem): Boolean = text.isBlank() ||
        item.title.ja.contains(text, ignoreCase = true) ||
        item.title.en.contains(text, ignoreCase = true) ||
        item.speaker.contains(text, ignoreCase = true)
}
