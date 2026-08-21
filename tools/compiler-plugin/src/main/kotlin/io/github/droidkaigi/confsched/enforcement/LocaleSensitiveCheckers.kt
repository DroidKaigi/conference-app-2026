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
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal object LocaleSensitiveNames {
    val LOCALE_SENSITIVE_CLASS_ID = ClassId(
        FqName("io.github.droidkaigi.confsched.core.common"),
        Name.identifier("LocaleSensitive"),
    )

    // Both render one preview per translated locale; LocaleScreenPreviews adds a phone-sized frame.
    val EVERY_LOCALE_PREVIEW_CLASS_IDS: Set<ClassId> = setOf(
        ClassId(
            FqName("io.github.droidkaigi.confsched.core.preview"),
            Name.identifier("LocalePreviews"),
        ),
        ClassId(
            FqName("io.github.droidkaigi.confsched.core.preview"),
            Name.identifier("LocaleScreenPreviews"),
        ),
    )

    // Every value of these types is resolved against the active locale, so referencing one makes a
    // function inherently locale-sensitive. The types are the anchor rather than the reading
    // functions (`stringResource`, `getString`, …) because the generated `Res` accessors are
    // internal and regenerated per module, leaving no stable declaration to match on.
    private val RESOURCES_PACKAGE = FqName("org.jetbrains.compose.resources")
    private val MODEL_PACKAGE = FqName("io.github.droidkaigi.confsched.core.model")
    val LOCALIZED_TEXT_CLASS_IDS: Set<ClassId> = setOf(
        ClassId(RESOURCES_PACKAGE, Name.identifier("StringResource")),
        ClassId(RESOURCES_PACKAGE, Name.identifier("PluralStringResource")),
        ClassId(RESOURCES_PACKAGE, Name.identifier("StringArrayResource")),
        ClassId(MODEL_PACKAGE, Name.identifier("MultiLangText")),
    )
}

// A preview is locale-sensitive when its body transitively reaches a localized resource; see
// SensitivityAnalysis for how that reachability is decided.
internal object LocaleSensitivePreviewChecker : FirSimpleFunctionChecker(MppCheckerKind.Platform) {

    private val analysis = SensitivityAnalysis(
        markerClassId = LocaleSensitiveNames.LOCALE_SENSITIVE_CLASS_ID,
        isInherentRead = ::readsLocalizedResource,
    )

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val session = context.session
        val symbol = declaration.symbol
        if (!symbol.hasAnnotation(PreviewWrapperNames.COMPOSABLE_ID, session)) return
        if (!symbol.previewAnnotations(session).isPreview) return

        val body = declaration.body ?: return
        if (!analysis.isReachedFrom(body)) return

        if (LocaleSensitiveNames.EVERY_LOCALE_PREVIEW_CLASS_IDS.any { symbol.hasAnnotation(it, session) }) return
        reporter.reportOn(
            declaration.source,
            LocaleSensitiveErrors.LOCALE_SENSITIVE_PREVIEW_REQUIRES_EVERY_LOCALE,
            context,
        )
    }

    private fun readsLocalizedResource(symbol: FirCallableSymbol<*>): Boolean =
        symbol.resolvedReturnType.classId in LocaleSensitiveNames.LOCALIZED_TEXT_CLASS_IDS
}

object LocaleSensitiveErrors : KtDiagnosticsContainer() {
    val LOCALE_SENSITIVE_PREVIEW_REQUIRES_EVERY_LOCALE by error0<PsiElement>(SourceElementPositioningStrategies.DEFAULT)

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = LocaleSensitiveErrorMessages
}

object LocaleSensitiveErrorMessages : BaseDiagnosticRendererFactory() {
    @Suppress("ktlint:standard:property-naming")
    override val MAP by KtDiagnosticFactoryToRendererMap("LocaleSensitive") { map ->
        map.put(
            LocaleSensitiveErrors.LOCALE_SENSITIVE_PREVIEW_REQUIRES_EVERY_LOCALE,
            "This preview renders @LocaleSensitive content, so it must be previewed under every " +
                "locale the app translates: replace `@Preview` with `@LocalePreviews` " +
                "(`@LocaleScreenPreviews` for a screen-level preview).",
        )
    }
}
