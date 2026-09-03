package io.github.droidkaigi.confsched.core.testing

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.droidkaigi.confsched.core.model.buildconfig.BuildConfigProvider

@Inject
@SingleIn(TestingScope::class)
@ContributesBinding(TestingScope::class)
class FakeBuildConfigProvider : BuildConfigProvider {
    override val versionName: String = "1.0.0"
}
