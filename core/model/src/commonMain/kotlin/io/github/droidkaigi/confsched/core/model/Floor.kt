package io.github.droidkaigi.confsched.core.model

enum class Floor(val label: String) {
    Ground("1F"),
    Basement("B1F"),
    ;

    fun toggle(): Floor = when (this) {
        Ground -> Basement
        Basement -> Ground
    }
}
