package io.github.droidkaigi.confsched.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.droidkaigi.confsched.core.common.context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        submitDeepLink(intent)
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
