package io.github.droidkaigi.confsched.core.common

import androidx.navigation3.runtime.NavKey

/**
 * Marks a destination that hosts app startup flow and leaves the back stack once it completes.
 * Deep-link resolution waits until no [StartupNavKey] remains on the stack.
 */
interface StartupNavKey : NavKey
