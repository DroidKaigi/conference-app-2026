package io.github.droidkaigi.confsched.app

import androidx.activity.ComponentActivity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.lang.ref.WeakReference

/**
 * The activity the reader is looking at, for the work that only an activity can do — asking for a
 * runtime permission, opening a system settings screen.
 */
@Inject
@SingleIn(AppScope::class)
class CurrentActivityHolder {
    // Weak: the holder outlives every activity, and one already destroyed must not be kept alive
    // by a reference that nothing clears.
    private val reference = MutableStateFlow<WeakReference<ComponentActivity>?>(null)

    val current: ComponentActivity? get() = reference.value?.get()

    /** Emits the current activity and every one that replaces it, so a caller can follow a recreation. */
    val currentFlow: Flow<ComponentActivity?> = reference.map { it?.get() }

    fun register(activity: ComponentActivity) {
        reference.value = WeakReference(activity)
    }

    fun unregister(activity: ComponentActivity) {
        // A recreation can destroy the previous activity after its replacement registered.
        if (current === activity) reference.value = null
    }
}
