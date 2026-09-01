package io.github.droidkaigi.confsched.app

import android.content.Context

interface CurrentActivityDependencies {
    val currentActivityHolder: CurrentActivityHolder
}

val Context.currentActivityDependencies: CurrentActivityDependencies get() = appGraph as CurrentActivityDependencies
