package io.github.droidkaigi.confsched.core.model

/** The rooms sessions run in. */
enum class Room {
    NARWHAL,
    OTTER,
    PANDA,
    QUAIL,
    MEERKAT,

    /** A room the timetable names but this app does not know. */
    UNKNOWN,
    ;

    /** Where the venue puts the room, which the conference publishes as a map and not as data. */
    val floor: Floor?
        get() = when (this) {
            NARWHAL, MEERKAT -> Floor.Ground
            OTTER, PANDA, QUAIL -> Floor.Basement
            UNKNOWN -> null
        }

    companion object {
        /** The room [name] refers to, or [UNKNOWN] where it is none of the above. */
        fun of(name: String): Room =
            entries.firstOrNull { it != UNKNOWN && it.name.equals(name, ignoreCase = true) } ?: UNKNOWN
    }
}
