package io.github.droidkaigi.confsched.enforcement

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class EnforcementFirExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::EnforcementCheckersExtension
        registerDiagnosticContainers(
            NoDirectMutateErrors,
            RoleContextErrors,
            NavigatorErrors,
            MutationKeyErrors,
            PersistedKeyErrors,
            RememberSerializableErrors,
            SafeClickErrors,
            NoForwardOnlyActionErrors,
            SoilReadConfinementErrors,
            PreviewRequiresWrapperErrors,
            CallableReferenceErrors,
            MutationEffectResetErrors,
            PlatformOnlyErrors,
            ThemeSensitiveErrors,
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
            ForwardingMemberErrors,
        )
    }
}
