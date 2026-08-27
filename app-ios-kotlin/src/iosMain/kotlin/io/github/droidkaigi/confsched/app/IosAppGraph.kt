package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

// Swift Export bridges every public declaration of this module, and a Metro graph carries every
// @ContributesTo interface as a supertype; `internal` does not reach the declaration Swift Export
// reads, so the graph is hidden from the exported surface explicitly.
@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
@DependencyGraph(scope = AppScope::class)
internal interface IosAppGraph : AppGraph {
    val sessionReminderSync: SessionReminderSync

    @DependencyGraph.Factory
    fun interface Factory {
        // The Swift packages Xcode links are described by the iOS build, not by Gradle, so their
        // export arrives from the caller rather than from a resource this module owns.
        fun create(@Provides @SwiftPackageLicenses swiftPackageLicensesJson: String): IosAppGraph
    }
}
