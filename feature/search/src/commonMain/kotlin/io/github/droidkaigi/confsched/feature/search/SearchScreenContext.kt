package io.github.droidkaigi.confsched.feature.search

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.PresenterContext
import io.github.droidkaigi.confsched.core.common.ScreenContext
import io.github.droidkaigi.confsched.core.model.SearchScreenScope

@Inject
class SearchPresenterContext : PresenterContext

@Inject
@SingleIn(SearchScreenScope::class)
class SearchScreenContext(
    val presenterContext: SearchPresenterContext,
) : ScreenContext
