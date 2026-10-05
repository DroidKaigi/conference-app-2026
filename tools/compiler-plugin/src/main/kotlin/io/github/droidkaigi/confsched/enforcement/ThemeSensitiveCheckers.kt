package io.github.droidkaigi.confsched.enforcement

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirNamedFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal object ThemeSensitiveNames {
    val THEME_SENSITIVE_CLASS_ID = ClassId(
        FqName("io.github.droidkaigi.confsched.core.common"),
        Name.identifier("ThemeSensitive"),
    )

    // Reading any of these MaterialTheme members makes a function inherently theme-sensitive,
    // so such reads are automatic roots of the transitive detection (no annotation required).
    val MATERIAL_THEME_CLASS_ID = ClassId(
        FqName("androidx.compose.material3"),
        Name.identifier("MaterialTheme"),
    )
    val MATERIAL_THEME_GETTER_NAMES: Set<Name> =
        setOf("colorScheme", "typography", "shapes").mapTo(mutableSetOf(), Name::identifier)
}

// A preview is theme-sensitive when its body transitively reaches a MaterialTheme read; see
// SensitivityAnalysis for how that reachability is decided.
internal object ThemeSensitivePreviewChecker : FirNamedFunctionChecker(MppCheckerKind.Platform) {

    private val analysis = SensitivityAnalysis(
        markerClassId = ThemeSensitiveNames.THEME_SENSITIVE_CLASS_ID,
        isInherentRead = ::isMaterialThemeRead,
    )

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val session = context.session
        val symbol = declaration.symbol
        if (!symbol.hasAnnotation(PreviewWrapperNames.COMPOSABLE_ID, session)) return
        val preview = symbol.previewAnnotations(session)
        if (!preview.isPreview) return

        val body = declaration.body ?: return
        if (!analysis.isReachedFrom(body)) return

        val rendersEveryScheme = declaration.valueParameters.any { parameter ->
            parameter.symbol.resolvedCompilerAnnotationsWithClassIds
                .firstOrNull {
                    it.toAnnotationClassId(session) == PreviewWrapperNames.PREVIEW_PARAMETER_ANNOTATION_ID
                }
                ?.classArgument(PreviewWrapperNames.PROVIDER_ARGUMENT_NAME) ==
                PreviewWrapperNames.KAIGI_SCHEME_PROVIDER_ID
        }
        if (!rendersEveryScheme) {
            reporter.reportOn(
                declaration.source,
                ThemeSensitiveErrors.THEME_SENSITIVE_PREVIEW_REQUIRES_MULTI_THEME,
                context,
            )
        }
    }

    private fun isMaterialThemeRead(symbol: FirCallableSymbol<*>): Boolean {
        if (symbol !is FirPropertySymbol) return false
        val callableId = symbol.callableId ?: return false
        return callableId.classId == ThemeSensitiveNames.MATERIAL_THEME_CLASS_ID &&
            callableId.callableName in ThemeSensitiveNames.MATERIAL_THEME_GETTER_NAMES
    }
}

object ThemeSensitiveErrors : KtDiagnosticsContainer() {
    val THEME_SENSITIVE_PREVIEW_REQUIRES_MULTI_THEME by error0<PsiElement>(SourceElementPositioningStrategies.DEFAULT)

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = ThemeSensitiveErrorMessages
}

object ThemeSensitiveErrorMessages : BaseDiagnosticRendererFactory() {
    @Suppress("ktlint:standard:property-naming")
    override val MAP by KtDiagnosticFactoryToRendererMap("ThemeSensitive") { map ->
        map.put(
            ThemeSensitiveErrors.THEME_SENSITIVE_PREVIEW_REQUIRES_MULTI_THEME,
            "This preview renders @ThemeSensitive content, so it must be previewed under every " +
                "theme: give it a `@PreviewParameter(KaigiSchemeProvider::class) colorScheme: " +
                "KaigiColorScheme` parameter and open `KaigiPreviewTheme(colorScheme) { … }` in the body.",
        )
    }
}
