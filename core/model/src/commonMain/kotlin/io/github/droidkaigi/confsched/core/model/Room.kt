package io.github.droidkaigi.confsched.core.model

/** A place at the venue an event is held in. */
sealed interface Room {
    /** The name the venue signs the room with. */
    val label: MultiLangText

    /** Where the venue puts the room, or null where the venue map does not place it. */
    val floor: Floor?

    companion object {
        /** The [SessionRoom] whose name is [label]'s English side, or a [NamedRoom] where there is none. */
        fun of(label: MultiLangText): Room =
            SessionRoom.of(label.en).takeIf { it != SessionRoom.UNKNOWN } ?: NamedRoom(label)
    }
}

/** The rooms sessions run in. */
enum class SessionRoom : Room {
    NARWHAL,
    OTTER,
    PANDA,
    QUAIL,
    MEERKAT,

    /** A room the timetable names but this app does not know. */
    UNKNOWN,
    ;

    override val label: MultiLangText = MultiLangText(ja = name, en = name)

    /** Where the venue puts the room, which the conference publishes as a map and not as data. */
    override val floor: Floor?
        get() = when (this) {
            NARWHAL, MEERKAT -> Floor.Ground
            OTTER, PANDA, QUAIL -> Floor.Basement
            UNKNOWN -> null
        }

    companion object {
        /** The room [name] refers to, or [UNKNOWN] where it is none of the above. */
        fun of(name: String): SessionRoom =
            entries.firstOrNull { it != UNKNOWN && it.name.equals(name, ignoreCase = true) } ?: UNKNOWN
    }
}

/** A room the conference publishes only by name, such as where a project is exhibited. */
data class NamedRoom(override val label: MultiLangText) : Room {
    override val floor: Floor?
        get() = null
}

/** The room's English name with its floor where the venue map places it, e.g. `OTTER (B1F)`. */
val Room.locationText: String get() = floor?.let { "${label.en} (${it.label})" } ?: label.en
