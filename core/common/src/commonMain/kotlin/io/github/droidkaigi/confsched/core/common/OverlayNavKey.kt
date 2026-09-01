package io.github.droidkaigi.confsched.core.common

import androidx.navigation3.runtime.NavKey

/**
 * The key of a screen whose entry renders as an overlay — a dialog, say — above the scene the
 * entries below it form.
 *
 * The entry must carry overlay metadata such as `DialogSceneStrategy.dialog()`, and the shell
 * leaves such an entry out of the panes it counts, since the scene under the overlay does not
 * draw it.
 */
interface OverlayNavKey : NavKey
