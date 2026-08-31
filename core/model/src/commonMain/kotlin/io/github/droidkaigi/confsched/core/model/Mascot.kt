package io.github.droidkaigi.confsched.core.model

enum class Mascot {
    A,
    B,
    C,
    D,
    E,
    F,
}

/** The mascot the design assigns to the room's session cards; [Mascot.F] doubles as the fallback for rooms without one of their own. */
val SessionRoom.mascot: Mascot
    get() = when (this) {
        SessionRoom.NARWHAL -> Mascot.E
        SessionRoom.OTTER -> Mascot.B
        SessionRoom.PANDA -> Mascot.A
        SessionRoom.QUAIL -> Mascot.C
        SessionRoom.MEERKAT, SessionRoom.UNKNOWN -> Mascot.F
    }
