package io.github.droidkaigi.confsched.enforcement

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirNamedFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.primaryConstructorIfAny
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.isUnit
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal object NavigatorNames {
    private val COMMON_PACKAGE = FqName("io.github.droidkaigi.confsched.core.common")

    val NAVIGATOR_ID = ClassId(COMMON_PACKAGE, Name.identifier("Navigator"))
    val COMPOSABLE_ID = ClassId(FqName("androidx.compose.runtime"), Name.identifier("Composable"))

    val NAV_INFRA_PACKAGE = COMMON_PACKAGE

    val STATE_NAME_SUFFIXES = listOf("UiState", "Action", "ActionResult")

    const val PRESENTER_NAME_SUFFIX = "Presenter"
}

private fun FirClassLikeSymbol<*>.superTypeClassIds(session: FirSession): Set<ClassId> =
    lookupSuperTypes(this, lookupInterfaces = true, deep = true, useSiteSession = session)
        .mapNotNullTo(mutableSetOf()) { it.classId }

private fun FirCallableSymbol<*>.isNavigatorTyped(session: FirSession): Boolean {
    val classSymbol = resolvedReturnTypeRef.toRegularClassSymbol(session) ?: return false
    return classSymbol.classId == NavigatorNames.NAVIGATOR_ID ||
        NavigatorNames.NAVIGATOR_ID in classSymbol.superTypeClassIds(session)
}

private fun FirCallableSymbol<*>.declaresContextOf(target: ClassId, session: FirSession): Boolean =
    contextParameterSymbols.any { param ->
        val classSymbol = param.resolvedReturnTypeRef.toRegularClassSymbol(session) ?: return@any false
        classSymbol.classId == target || target in classSymbol.superTypeClassIds(session)
    }

internal object NavigatorConfinedToNavEntryClassChecker : FirClassChecker(MppCheckerKind.Platform) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirClass) {
        val session = context.session
        val symbol = declaration.symbol
        val ancestry = symbol.superTypeClassIds(session)

        val isRoleContext =
            RoleContextNames.SCREEN_CONTEXT_ID in ancestry ||
                RoleContextNames.PRESENTER_CONTEXT_ID in ancestry
        val isStateType = NavigatorNames.STATE_NAME_SUFFIXES.any { suffix ->
            symbol.classId.shortClassName.asString().endsWith(suffix)
        }
        if (!isRoleContext && !isStateType) return

        val reported = mutableSetOf<Pair<Int, Int>?>()

        fun reportIfNavigator(callable: FirCallableSymbol<*>) {
            if (!callable.isNavigatorTyped(session)) return
            val source = callable.source ?: declaration.source
            val key = source?.let { it.startOffset to it.endOffset }
            if (!reported.add(key)) return
            reporter.reportOn(source, NavigatorErrors.NAVIGATOR_NOT_CONFINED, context)
        }

        declaration.primaryConstructorIfAny(session)?.valueParameterSymbols?.forEach(::reportIfNavigator)
        symbol.processAllDeclarations(session) { member ->
            if (member is FirPropertySymbol) reportIfNavigator(member)
        }
    }
}

internal object NavigatorConfinedToNavEntryFunctionChecker : FirSimpleFunctionChecker(MppCheckerKind.Platform) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirNamedFunction) {
        val session = context.session
        val symbol = declaration.symbol
        if (!symbol.hasAnnotation(NavigatorNames.COMPOSABLE_ID, session)) return

        val isPresenter = symbol.name.asString().endsWith(NavigatorNames.PRESENTER_NAME_SUFFIX)
        val returnsUnit = symbol.resolvedReturnTypeRef.coneType.isUnit
        val inNavInfra = symbol.callableId?.packageName == NavigatorNames.NAV_INFRA_PACKAGE
        val inScope = isPresenter || (returnsUnit && !inNavInfra)
        if (!inScope) return

        for (param in symbol.valueParameterSymbols) {
            if (param.isNavigatorTyped(session)) {
                reporter.reportOn(param.source ?: declaration.source, NavigatorErrors.NAVIGATOR_NOT_CONFINED, context)
            }
        }
        if (symbol.isNavigatorTyped(session)) {
            reporter.reportOn(declaration.source, NavigatorErrors.NAVIGATOR_NOT_CONFINED, context)
        }
    }
}
