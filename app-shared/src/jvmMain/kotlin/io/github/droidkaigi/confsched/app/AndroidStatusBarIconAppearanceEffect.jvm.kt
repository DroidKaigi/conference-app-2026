package io.github.droidkaigi.confsched.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.droidkaigi.confsched.core.common.PlatformOnly
import io.github.droidkaigi.confsched.core.common.TargetPlatform

@PlatformOnly(TargetPlatform.Android)
@Composable
internal actual fun AndroidStatusBarIconAppearanceEffect(bandColor: Color) = Unit
