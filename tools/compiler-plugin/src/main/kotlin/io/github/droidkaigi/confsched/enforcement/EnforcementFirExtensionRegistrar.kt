package io.github.droidkaigi.confsched.enforcement

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class EnforcementFirExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::EnforcementCheckersExtension
        +::ComposableEmissionKinds
        registerDiagnosticContainers(
            NoDirectMutateErrors,
            RoleContextErrors,
            NavigatorErrors,
            MutationKeyErrors,
            PersistedKeyErrors,
            RememberSerializableErrors,
            NoForwardOnlyActionErrors,
            SoilReadConfinementErrors,
            PreviewRequiresWrapperErrors,
            CallableReferenceErrors,
            MutationEffectResetErrors,
            PlatformOnlyErrors,
            ThemeSensitiveErrors,
            LocaleSensitiveErrors,
            ScreenFileErrors,
            ComposableNestingErrors,
            PassThroughLambdaErrors,
            ExplicitBackingFieldErrors,
            PrivateSetErrors,
            UiComponentPreviewErrors,
            UiComponentParameterErrors,
            CallbackArgumentErrors,
            ComposableTrailingLambdaErrors,
            RememberBindingErrors,
            StateDelegationErrors,
            RootEmissionErrors,
        )
    }
}
