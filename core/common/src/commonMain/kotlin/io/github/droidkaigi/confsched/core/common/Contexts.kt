package io.github.droidkaigi.confsched.core.common

interface PresenterContext {
    val logger: KaigiLogger
}

interface ScreenContext : SoilDataContext {
    val logger: KaigiLogger
}

interface SoilDataContext
