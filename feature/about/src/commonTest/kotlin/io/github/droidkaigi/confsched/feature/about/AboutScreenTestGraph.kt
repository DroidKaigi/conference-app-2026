package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import io.github.droidkaigi.confsched.core.model.AboutScreenScope
import io.github.droidkaigi.confsched.core.model.buildconfig.BuildConfigProvider
import io.github.droidkaigi.confsched.core.testing.TestingScope

const val TEST_VERSION_NAME = "1.0.0-test"

@DependencyGraph(scope = TestingScope::class, additionalScopes = [AboutScreenScope::class])
interface AboutScreenTestGraph {
    val screenContext: AboutScreenContext
    val presenterContext: AboutPresenterContext

    @Provides
    fun buildConfigProvider(): BuildConfigProvider = object : BuildConfigProvider {
        override val versionName: String = TEST_VERSION_NAME
    }
}
