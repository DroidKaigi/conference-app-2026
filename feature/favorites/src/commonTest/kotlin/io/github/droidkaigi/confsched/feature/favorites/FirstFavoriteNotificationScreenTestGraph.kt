package io.github.droidkaigi.confsched.feature.favorites

import dev.zacsweers.metro.DependencyGraph
import io.github.droidkaigi.confsched.core.testing.FakeFirstFavoriteGuidanceMutationKey
import io.github.droidkaigi.confsched.core.testing.FakeNotificationPermissionMutationKey
import io.github.droidkaigi.confsched.core.testing.TestingScope

@DependencyGraph(scope = TestingScope::class)
interface FirstFavoriteNotificationScreenTestGraph {
    val presenterContext: FirstFavoriteNotificationPresenterContext
    val guidanceMutationKey: FakeFirstFavoriteGuidanceMutationKey
    val notificationPermissionMutationKey: FakeNotificationPermissionMutationKey
}
