package io.github.droidkaigi.confsched.core.ui

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// No motion sensor on this platform, so the source stays at DeviceTilt.Level.
@Composable
internal actual fun rememberDeviceTilts(): Flow<DeviceTilt> = emptyFlow()
