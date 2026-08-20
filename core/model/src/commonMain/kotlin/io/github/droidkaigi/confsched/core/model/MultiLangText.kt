package io.github.droidkaigi.confsched.core.model

/**
 * Text the conference API supplies in both of its languages. The language is chosen where the text
 * is displayed rather than where it is fetched, so a cached response outlives a locale change.
 */
data class MultiLangText(val ja: String, val en: String) {
    fun of(language: DisplayLanguage): String = when (language) {
        DisplayLanguage.Japanese -> ja
        DisplayLanguage.English -> en
    }
}

/** Which side of a [MultiLangText] the reader is shown; a session's own [Language] is another thing. */
enum class DisplayLanguage {
    Japanese,
    English,
    ;

    fun toggled(): DisplayLanguage = if (this == Japanese) English else Japanese
}
