package io.github.droidkaigi.confsched.app

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.common.AppNavigator
import io.github.droidkaigi.confsched.core.common.DefaultScreenNavigator
import io.github.droidkaigi.confsched.core.model.StaffScreenScope
import io.github.droidkaigi.confsched.feature.staff.StaffScreenNavigator

@Inject
@SingleIn(StaffScreenScope::class)
@ContributesBinding(StaffScreenScope::class)
class DefaultStaffScreenNavigator(
    appNavigator: AppNavigator,
) : DefaultScreenNavigator(appNavigator), StaffScreenNavigator
