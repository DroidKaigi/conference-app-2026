@file:OptIn(org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi::class)

package io.github.droidkaigi.confsched.enforcement

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

class EnforcementCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val pluginId: String = PluginNames.PLUGIN_ID
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        IrGenerationExtension.registerExtension(ThemeSensitiveMetadataIrExtension())
        IrGenerationExtension.registerExtension(LocaleSensitiveMetadataIrExtension())
        FirExtensionRegistrarAdapter.registerExtension(EnforcementFirExtensionRegistrar())
    }
}
