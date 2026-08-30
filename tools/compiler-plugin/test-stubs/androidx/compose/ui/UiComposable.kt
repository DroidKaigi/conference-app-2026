package androidx.compose.ui

import androidx.compose.runtime.ComposableTargetMarker

@ComposableTargetMarker(description = "UI Composable")
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
annotation class UiComposable
