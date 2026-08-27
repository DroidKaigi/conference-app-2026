package io.github.droidkaigi.confsched.feature.eventmap

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.KaigiLogger
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.EventMapScreenScope
import io.github.droidkaigi.confsched.core.model.ProjectsQueryKey

@Inject
class EventMapPresenterContext(override val logger: KaigiLogger) : PresenterContext

@Inject
@SingleIn(EventMapScreenScope::class)
class EventMapScreenContext(
    val projectsQueryKey: ProjectsQueryKey,
    override val logger: KaigiLogger,
    val presenterContext: EventMapPresenterContext,
) : ScreenContext
