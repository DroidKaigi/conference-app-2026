package io.github.droidkaigi.confsched.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.UiComposable

private interface Overlay {
    @Composable
    fun Content()
}

private object ClockOverlay : Overlay {
    @Composable
    override fun <!ABSTRACT_COMPOSABLE_EMITS_WITHOUT_APPLIER!>Content<!>() {
        Text("clock")
    }
}

private interface AnnotatedOverlay {
    @Composable
    @UiComposable
    fun Content()
}

private object BadgeOverlay : AnnotatedOverlay {
    @Composable
    override fun Content() {
        Text("badge")
    }
}

private interface Monitor {
    @Composable
    fun Observe(label: String)
}

private object HistoryMonitor : Monitor {
    @Composable
    override fun Observe(label: String) {
        LaunchedEffect(label) {}
    }
}

private interface DefaultedOverlay {
    @Composable
    fun Content() {
        Text("default")
    }
}

private object CustomOverlay : DefaultedOverlay {
    @Composable
    override fun Content() {
        Text("custom")
    }
}

private fun interface LabelSlot {
    @Composable
    operator fun invoke(label: String)
}

@Composable
private fun Slotted(label: String, slot: LabelSlot) {
    slot(label)
}

@Composable
private fun PassesEmittingSlot(label: String) {
    Slotted(label) <!ABSTRACT_COMPOSABLE_EMITS_WITHOUT_APPLIER!>{ Text(it) }<!>
}

private fun interface AnnotatedLabelSlot {
    @Composable
    @UiComposable
    operator fun invoke(label: String)
}

@Composable
private fun AnnotatedSlotted(label: String, slot: AnnotatedLabelSlot) {
    slot(label)
}

@Composable
private fun PassesAnnotatedSlot(label: String) {
    AnnotatedSlotted(label) { Text(it) }
}
