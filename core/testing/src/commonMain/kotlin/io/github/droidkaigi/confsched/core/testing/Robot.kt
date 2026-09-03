package io.github.droidkaigi.confsched.core.testing

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.navigationevent.DirectNavigationEventInput
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import io.github.droidkaigi.confsched.core.common.LocalSnackbarHostState
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.preview.wrapper.KaigiPreviewTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import soil.query.SwrCachePlus
import soil.query.compose.SwrClientProvider

@OptIn(ExperimentalTestApi::class)
abstract class Robot(protected val composeUiTest: ComposeUiTest) {

    private val navigationEventInput = DirectNavigationEventInput()
    private val navigationEventDispatcherOwner = object : NavigationEventDispatcherOwner {
        override val navigationEventDispatcher = NavigationEventDispatcher()
    }

    init {
        navigationEventDispatcherOwner.navigationEventDispatcher.addInput(navigationEventInput)
    }

    /** Completes a system back gesture, which a screen may take for itself before the back stack does. */
    fun pressSystemBack() {
        navigationEventInput.backCompleted()
        composeUiTest.waitForIdle()
    }

    // Stands in for what a nav entry supplies in production: the Soil client and the snackbar host
    // from snackbarNavEntryDecorator, the navigation-event dispatcher the platform host owns, plus
    // the resolver that turns a fake's preview:// URL into a local drawable. A fresh client per call
    // lets a scenario set up more than once.
    protected fun setScreenContent(content: @Composable () -> Unit) {
        val clientScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val client = SwrCachePlus(clientScope)
        val snackbarHostState = SnackbarHostState()

        composeUiTest.setContent {
            // A held query leaves a coroutine suspended in this scope, and a scenario may set up
            // again, so the scope has to end with the composition that owns it.
            DisposableEffect(Unit) {
                onDispose(clientScope::cancel)
            }
            SwrClientProvider(client = client) {
                KaigiPreviewTheme(colorScheme = KaigiColorScheme.MorningMist) {
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides snackbarHostState,
                        LocalNavigationEventDispatcherOwner provides navigationEventDispatcherOwner,
                        content = content,
                    )
                }
            }
        }
        composeUiTest.waitForIdle()
    }
}

@OptIn(ExperimentalTestApi::class)
fun <R : Robot> runRobotTest(
    robotFactory: ComposeUiTest.() -> R,
    scenario: ScenarioBuilder<R>.() -> Unit,
) {
    val leaves = ScenarioBuilder<R>().apply(scenario).build().flatten()
    check(leaves.isNotEmpty()) { "Robot scenario has no itShould blocks" }
    for (leaf in leaves) {
        try {
            runRobotComposeUiTest {
                val robot = robotFactory()
                leaf.setups.forEach { step -> step(robot) }
                leaf.check(robot)
                // After the assertions, so the capture shows the state the scenario describes.
                captureRobotScreen(robot.screenshotPrefix() + leaf.screenshotName())
            }
        } catch (error: Throwable) {
            throw AssertionError("Scenario failed: ${leaf.name}", error)
        }
    }
}

// Scenario sentences repeat across screens — "and back is tapped ... leave the screen once" is on
// three of them — so the screen the Robot drives has to be part of the name.
private fun Robot.screenshotPrefix(): String =
    (this::class.simpleName ?: "Robot").removeSuffix("Robot") + "."

// A scenario name reads as a sentence, and the screenshot id travels through file names, git refs
// and a Markdown link, so everything outside the portable set collapses to an underscore.
private fun ScenarioLeaf<*>.screenshotName(): String =
    name.map { if (it.isLetterOrDigit() || it == '.' || it == '-') it else '_' }
        .joinToString("")
        .trim('_')
