package io.github.droidkaigi.confsched.core.model

/** What kind of slot a session occupies in the timetable. */
enum class SessionType {
    NORMAL,
    WELCOME_TALK,
    RESERVED,
    CODELABS,
    FIRESIDE_CHAT,
    LUNCH,
    BREAK,
    AFTER_PARTY,
    RECAP,
}
