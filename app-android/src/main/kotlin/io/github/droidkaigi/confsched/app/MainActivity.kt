package io.github.droidkaigi.confsched.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.droidkaigi.confsched.core.common.context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        // From API 30 the keyboard reports itself as an animating inset, so the window is left
        // as it is and the content makes room for it on its own; the padding then follows the
        // keyboard's animation rather than landing once the resize is over. Below 30 there is no
        // such inset to read, and the manifest's adjustResize stands.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        }
        super.onCreate(savedInstanceState)

        // Recreation — a configuration change or process death — always delivers a non-null
        // savedInstanceState and redelivers the task's original intent; the restored back stack
        // already reflects the link, so only a fresh creation consumes it.
        if (savedInstanceState == null) {
            submitDeepLink(intent)
        }
        setContent {
            context(appGraph) {
                KaigiApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        submitDeepLink(intent)
    }

    private fun submitDeepLink(intent: Intent) {
        intent.toDeepLink()?.let(appGraph.deepLinkStore::submit)
    }
}
