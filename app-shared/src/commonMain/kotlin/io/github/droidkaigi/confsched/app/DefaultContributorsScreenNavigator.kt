package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.ContributorsScreenScope
import io.github.droidkaigi.confsched.feature.contributors.ContributorsScreenNavigator

@Inject
@SingleIn(ContributorsScreenScope::class)
@ContributesBinding(ContributorsScreenScope::class)
class DefaultContributorsScreenNavigator(
    appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator), ContributorsScreenNavigator
