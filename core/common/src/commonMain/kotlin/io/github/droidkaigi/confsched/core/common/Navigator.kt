package io.github.droidkaigi.confsched.core.common

import androidx.navigation3.runtime.NavKey

interface Navigator {
    fun back(origin: NavKey? = null)
}
