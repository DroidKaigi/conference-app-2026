package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import io.github.droidkaigi.confsched.core.model.Floor
import io.github.droidkaigi.confsched.core.ui.generated.resources.Res
import io.github.droidkaigi.confsched.core.ui.generated.resources.event_map_1f
import io.github.droidkaigi.confsched.core.ui.generated.resources.event_map_b1f
import org.jetbrains.compose.resources.painterResource

@Composable
fun Floor.mapPainter(): Painter = painterResource(
    when (this) {
        Floor.Ground -> Res.drawable.event_map_1f
        Floor.Basement -> Res.drawable.event_map_b1f
    },
)
