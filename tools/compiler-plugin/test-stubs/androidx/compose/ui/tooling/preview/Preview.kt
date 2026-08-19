package androidx.compose.ui.tooling.preview

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Repeatable
annotation class Preview(val name: String = "", val locale: String = "", val widthDp: Int = -1, val heightDp: Int = -1)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class PreviewWrapper(val wrapper: KClass<*>)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.BINARY)
annotation class PreviewParameter(val provider: KClass<out PreviewParameterProvider<*>>)

interface PreviewParameterProvider<T> {
    val values: Sequence<T>
}
