package io.github.droidkaigi.confsched.enforcement

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirAnonymousFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirBasicDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFileChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirPropertyChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirSimpleFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirPropertyAccessExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirQualifiedAccessExpressionChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension

class EnforcementCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    override val expressionCheckers: ExpressionCheckers = object : ExpressionCheckers() {
        override val functionCallCheckers: Set<FirFunctionCallChecker> = setOf(
            NoPresenterEffectInScreenRootChecker,
            MustBeSerializableChecker,
            RememberSerializableChecker,
            NoForwardOnlyActionChecker,
            SoilReadConfinementChecker,
            MutationCallConfinementChecker,
            MutationEffectMustResetChecker,
            ComposableLambdaMustBeTrailingChecker,
        )
        override val propertyAccessExpressionCheckers: Set<FirPropertyAccessExpressionChecker> = setOf(
            NoDirectMutateChecker,
        )
        override val qualifiedAccessExpressionCheckers: Set<FirQualifiedAccessExpressionChecker> = setOf(
            RememberResultMustBeBoundChecker,
        )
    }

    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
        override val classCheckers: Set<FirClassChecker> = setOf(
            MutationKeyMustCarryTagChecker,
            NavigatorConfinedToNavEntryClassChecker,
            ScreenContextMustNotBePresenterContextChecker,
            ForwardingMemberMustDelegateChecker,
        )
        override val simpleFunctionCheckers: Set<FirSimpleFunctionChecker> = setOf(
            NavigatorConfinedToNavEntryFunctionChecker,
            PresenterMustNotDeclareScreenContextChecker,
            PreviewRequiresWrapperChecker,
            ThemeSensitivePreviewChecker,
            NavLambdaMustFlowToSafeClickChecker,
            ComposableNestingDepthChecker,
            UiComponentTakesWhatItReadsChecker,
            NoCallerSuppliedCallbackArgumentChecker,
        )
        override val anonymousFunctionCheckers: Set<FirAnonymousFunctionChecker> = setOf(
            LambdaCanBeCallableReferenceChecker,
            LambdaCanBePassedDirectlyChecker,
        )
        override val basicDeclarationCheckers: Set<FirBasicDeclarationChecker> = setOf(
            PlatformOnlyNamingChecker,
        )
        override val fileCheckers: Set<FirFileChecker> = setOf(
            ScreenIsSoleComponentInFileChecker,
            UiComponentRequiresPreviewChecker,
        )
        override val propertyCheckers: Set<FirPropertyChecker> = setOf(
            ExplicitBackingFieldRequiredChecker,
            PrivateSetRequiredChecker,
        )
    }
}
