package io.github.droidkaigi.confsched.enforcement

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.customAnnotations
import org.jetbrains.kotlin.fir.types.functionTypeKind
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal val COMPOSABLE_ANNOTATION_ID = ClassId(FqName("androidx.compose.runtime"), Name.identifier("Composable"))

// A registered @Composable function-type kind carries the annotation on the type's `invoke`; without
// that kind the type stays a kotlin.FunctionN holding @Composable as a type annotation.
internal fun ConeKotlinType.isComposableFunctionType(session: FirSession): Boolean {
    if (functionTypeKind(session)?.annotationOnInvokeClassId == COMPOSABLE_ANNOTATION_ID) return true
    return customAnnotations.any { it.toAnnotationClassId(session) == COMPOSABLE_ANNOTATION_ID }
}
