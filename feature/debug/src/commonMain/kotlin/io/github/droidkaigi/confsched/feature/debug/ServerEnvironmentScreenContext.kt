package io.github.droidkaigi.confsched.feature.debug

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.data.ServerEnvironmentSelectionMutationKey
import io.github.droidkaigi.confsched.core.model.ServerEnvironmentScreenScope

@Inject
class ServerEnvironmentPresenterContext(
    val debugPreferencesStore: DebugPreferencesStore,
    val serverEnvironmentSelectionMutationKey: ServerEnvironmentSelectionMutationKey,
    override val logger: KaigiLogger,
) : PresenterContext

@Inject
@SingleIn(ServerEnvironmentScreenScope::class)
class ServerEnvironmentScreenContext(
    override val logger: KaigiLogger,
    val presenterContext: ServerEnvironmentPresenterContext,
) : ScreenContext
