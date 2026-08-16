package io.github.droidkaigi.confsched.feature.about

import dev.zacsweers.metro.createGraph
import io.github.droidkaigi.confsched.core.testing.runPresenterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AboutScreenPresenterTest {

    private val graph = createGraph<AboutScreenTestGraph>()

    @Test
    fun the_build_version_name_reaches_the_ui_state() {
        runPresenterTest<AboutPresenterContext, Unit, Unit, AboutScreenUiState>(
            presenterContext = graph.presenterContext,
            presenter = { _ -> aboutScreenPresenter() },
        ) {
            assertEquals(TEST_VERSION_NAME, uiStates.awaitItem().versionName)
        }
    }
}
