package io.github.droidkaigi.confsched.feature.profilecard

import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import io.github.droidkaigi.confsched.core.common.NavEntryProvider
import io.github.droidkaigi.confsched.core.common.UiScope
import io.github.droidkaigi.confsched.core.common.context
import io.github.droidkaigi.confsched.core.common.instantNavTransition

@ContributesIntoSet(UiScope::class)
@Inject
class ProfileCardNavEntryProvider(
    private val screenGraphFactory: ProfileCardScreenGraph.Factory,
) : NavEntryProvider {
    override fun EntryProviderScope<NavKey>.register() {
        entry<ProfileCardNavKey>(metadata = instantNavTransition()) {
            val graph = retain(screenGraphFactory::createProfileCardScreenGraph)
            context(graph.screenContext) {
                ProfileCardScreenRoot()
            }
        }
    }
}
