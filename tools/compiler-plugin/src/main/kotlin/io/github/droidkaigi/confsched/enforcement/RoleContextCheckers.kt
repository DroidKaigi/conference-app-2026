package io.github.droidkaigi.confsched.enforcement

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirNamedFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal object RoleContextNames {
    private val PACKAGE = FqName("io.github.droidkaigi.confsched.core.common")

    val SCREEN_CONTEXT_ID = ClassId(PACKAGE, Name.identifier("ScreenContext"))
    val PRESENTER_CONTEXT_ID = ClassId(PACKAGE, Name.identifier("PresenterContext"))

    const val PRESENTER_NAME_SUFFIX = "Presenter"
}

private fun FirClassLikeSymbol<*>.superTypeClassIds(session: FirSession): Set<ClassId> =
    lookupSuperTypes(this, lookupInterfaces = true, deep = true, useSiteSession = session)
        .mapNotNullTo(mutableSetOf()) { it.classId }

private fun FirCallableSymbol<*>.declaresContextOf(target: ClassId, session: FirSession): Boolean =
    contextParameterSymbols.any { param ->
        val classSymbol = param.resolvedReturnTypeRef.toRegularClassSymbol(session) ?: return@any false
        classSymbol.classId == target || target in classSymbol.superTypeClassIds(session)
    }

internal object PresenterMustNotDeclareScreenContextChecker : FirNamedFunctionChecker(MppCheckerKind.Platform) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val session = context.session
        val symbol = declaration.symbol
        if (!symbol.declaresContextOf(RoleContextNames.PRESENTER_CONTEXT_ID, session)) return

        for (param in symbol.contextParameterSymbols) {
            val classSymbol = param.resolvedReturnTypeRef.toRegularClassSymbol(session) ?: continue
            val isScreenContext =
                classSymbol.classId == RoleContextNames.SCREEN_CONTEXT_ID ||
                    RoleContextNames.SCREEN_CONTEXT_ID in classSymbol.superTypeClassIds(session)
            if (isScreenContext) {
                reporter.reportOn(
                    param.source ?: declaration.source,
                    RoleContextErrors.PRESENTER_DECLARES_SCREEN_CONTEXT,
                    context,
                )
            }
        }
    }
}

internal object ScreenContextMustNotBePresenterContextChecker : FirClassChecker(MppCheckerKind.Platform) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirClass) {
        val session = context.session
        val ancestry = declaration.symbol.superTypeClassIds(session)
        val implementsScreen = RoleContextNames.SCREEN_CONTEXT_ID in ancestry
        val implementsPresenter = RoleContextNames.PRESENTER_CONTEXT_ID in ancestry
        if (implementsScreen && implementsPresenter) {
            reporter.reportOn(declaration.source, RoleContextErrors.SCREEN_CONTEXT_IS_PRESENTER_CONTEXT, context)
        }
    }
}

internal object NoPresenterEffectInScreenRootChecker : FirFunctionCallChecker(MppCheckerKind.Platform) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirFunctionCall) {
        val session = context.session

        val inScreenRoot = context.containingDeclarations.any { symbol ->
            symbol is FirCallableSymbol<*> &&
                symbol.declaresContextOf(RoleContextNames.SCREEN_CONTEXT_ID, session) &&
                !symbol.declaresContextOf(RoleContextNames.PRESENTER_CONTEXT_ID, session)
        }
        if (!inScreenRoot) return

        val callee = expression.toResolvedCallableSymbol() ?: return

        if (callee.name.asString().endsWith(RoleContextNames.PRESENTER_NAME_SUFFIX)) return

        if (callee.declaresContextOf(RoleContextNames.PRESENTER_CONTEXT_ID, session)) {
            reporter.reportOn(expression.source, RoleContextErrors.PRESENTER_EFFECT_IN_SCREEN_ROOT, context)
        }
    }
}
