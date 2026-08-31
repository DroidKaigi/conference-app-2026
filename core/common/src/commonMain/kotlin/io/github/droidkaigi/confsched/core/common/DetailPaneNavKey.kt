package io.github.droidkaigi.confsched.core.common

import androidx.navigation3.runtime.NavKey

/**
 * The key of a screen a list opens as the detail of a list-detail pair.
 *
 * The back stack holds at most one such entry at a time: a push of one over another replaces
 * the top instead of stacking, so a reader moving between details never accumulates them.
 */
interface DetailPaneNavKey : NavKey
