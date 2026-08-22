package io.github.droidkaigi.confsched.core.model

/** The rooms sessions run in. */
enum class Room {
    NARWHAL,
    OTTER,
    PANDA,
    QUAIL,
    MEERKAT,
    ;

    companion object {
        /** The room [name] refers to, or null where it is none of the above. */
        fun of(name: String): Room? =
            entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}
