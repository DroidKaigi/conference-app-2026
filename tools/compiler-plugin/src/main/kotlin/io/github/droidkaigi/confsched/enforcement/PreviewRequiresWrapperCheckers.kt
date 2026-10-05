package io.github.droidkaigi.confsched.enforcement

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirNamedFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirGetClassCall
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal object PreviewWrapperNames {
    val COMPOSABLE_ID = ClassId(FqName("androidx.compose.runtime"), Name.identifier("Composable"))
    val JETBRAINS_PREVIEW_ID =
        ClassId(FqName("org.jetbrains.compose.ui.tooling.preview"), Name.identifier("Preview"))
    val ANDROIDX_PREVIEW_ID =
        ClassId(FqName("androidx.compose.ui.tooling.preview"), Name.identifier("Preview"))
    val PREVIEW_IDS = setOf(JETBRAINS_PREVIEW_ID, ANDROIDX_PREVIEW_ID)

    val PREVIEW_WRAPPER_ANNOTATION_ID =
        ClassId(FqName("androidx.compose.ui.tooling.preview"), Name.identifier("PreviewWrapper"))
    val WRAPPER_ARGUMENT_NAME = Name.identifier("wrapper")

    val PREVIEW_PARAMETER_ANNOTATION_ID =
        ClassId(FqName("androidx.compose.ui.tooling.preview"), Name.identifier("PreviewParameter"))
    val PROVIDER_ARGUMENT_NAME = Name.identifier("provider")

    val KAIGI_SCHEME_PROVIDER_ID = ClassId(
        FqName("io.github.droidkaigi.confsched.core.preview"),
        Name.identifier("KaigiSchemeProvider"),
    )

    const val KAIGI_PREVIEW_THEME = "KaigiPreviewTheme"
}

internal class PreviewAnnotations(val isPreview: Boolean, val wrapper: ClassId?)

/**
 * `@Preview` and `@PreviewWrapper` also target annotation classes, so the tooling honours them on a
 * multi-preview annotation. One level of indirection is resolved here.
 */
internal fun FirNamedFunctionSymbol.previewAnnotations(session: FirSession): PreviewAnnotations {
    var isPreview = false
    var wrapper: ClassId? = null

    fun scan(annotations: List<FirAnnotation>) {
        for (annotation in annotations) {
            val classId = annotation.toAnnotationClassId(session)
            if (classId in PreviewWrapperNames.PREVIEW_IDS) isPreview = true
            if (classId == PreviewWrapperNames.PREVIEW_WRAPPER_ANNOTATION_ID) {
                wrapper = annotation.classArgument(PreviewWrapperNames.WRAPPER_ARGUMENT_NAME)
            }
        }
    }

    val declared = resolvedCompilerAnnotationsWithClassIds
    scan(declared)
    for (annotation in declared) {
        val classId = annotation.toAnnotationClassId(session) ?: continue
        val annotationClass = session.symbolProvider.getClassLikeSymbolByClassId(classId)
        if (annotationClass is FirRegularClassSymbol) {
            scan(annotationClass.resolvedCompilerAnnotationsWithClassIds)
        }
    }
    return PreviewAnnotations(isPreview, wrapper)
}

internal fun FirAnnotation.classArgument(name: Name): ClassId? =
    (argumentMapping.mapping[name] as? FirGetClassCall)
        ?.resolvedType?.typeArguments?.getOrNull(0)?.type?.classId

internal object PreviewRequiresWrapperChecker : FirNamedFunctionChecker(MppCheckerKind.Platform) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val session = context.session
        val symbol = declaration.symbol
        if (!symbol.hasAnnotation(PreviewWrapperNames.COMPOSABLE_ID, session)) return

        val preview = symbol.previewAnnotations(session)
        if (!preview.isPreview) return
        if (preview.wrapper != null) return

        // The other route: a preview that picks its own colour scheme cannot take one through the
        // wrapper, so it opens KaigiPreviewTheme itself.
        val topLevelCall = declaration.body?.statements?.firstOrNull()?.unwrapReturn() as? FirFunctionCall
        val opensPreviewTheme = topLevelCall?.toResolvedCallableSymbol()?.name?.asString() ==
            PreviewWrapperNames.KAIGI_PREVIEW_THEME
        if (!opensPreviewTheme) {
            reporter.reportOn(declaration.source, PreviewRequiresWrapperErrors.PREVIEW_WITHOUT_WRAPPER, context)
        }
    }

    private fun FirStatement.unwrapReturn(): FirStatement =
        if (this is FirReturnExpression) result else this
}

object PreviewRequiresWrapperErrors : KtDiagnosticsContainer() {
    val PREVIEW_WITHOUT_WRAPPER by error0<PsiElement>(SourceElementPositioningStrategies.DEFAULT)

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = PreviewRequiresWrapperErrorMessages
}

object PreviewRequiresWrapperErrorMessages : BaseDiagnosticRendererFactory() {
    @Suppress("ktlint:standard:property-naming")
    override val MAP by KtDiagnosticFactoryToRendererMap("PreviewRequiresWrapper") { map ->
        map.put(
            PreviewRequiresWrapperErrors.PREVIEW_WITHOUT_WRAPPER,
            "A @Preview @Composable must render inside KaigiTheme with the preview image resolver: " +
                "annotate it with `@PreviewWrapper(wrapper = KaigiPreviewWrapper::class)`, or make " +
                "the body's top-level statement a `KaigiPreviewTheme(colorScheme) { … }` call when " +
                "the preview picks its own colour scheme.",
        )
    }
}
