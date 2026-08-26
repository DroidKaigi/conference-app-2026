package io.github.droidkaigi.confsched.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class SoilIdsGenerator(
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    // getAllFiles() returns every file on every round; guard against re-emitting SoilIds (FileAlreadyExistsException on round 2).
    private var emitted = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (emitted) return emptyList()

        val keys = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSTypeAlias>()
            .mapNotNull { alias -> alias.toSoilKey() }
            .sortedBy { it.propertyName }
            .toList()

        if (keys.isEmpty()) return emptyList()

        val packageName = commonPackagePrefix(keys.map { it.packageName })

        val objectBuilder = TypeSpec.objectBuilder("SoilIds")
        keys.forEach { key ->
            when (key.kind) {
                SoilKeyKind.QUERY, SoilKeyKind.SUBSCRIPTION -> {
                    val idType = key.kind.idClass.parameterizedBy(key.typeArguments)
                    objectBuilder.addProperty(
                        PropertySpec.builder(key.propertyName, idType)
                            .initializer("%T(%S)", key.kind.idClass, key.namespace)
                            .build(),
                    )
                }

                SoilKeyKind.MUTATION -> {
                    val idType = key.kind.idClass.parameterizedBy(key.typeArguments)
                    objectBuilder.addFunction(
                        FunSpec.builder(key.propertyName)
                            .addParameter(ParameterSpec.builder("extraTag", MUTATION_TAG).build())
                            .returns(idType)
                            .addStatement("return %T(%S, extraTag.value)", key.kind.idClass, key.namespace)
                            .build(),
                    )
                }
            }
        }

        FileSpec.builder(packageName, "SoilIds")
            .addType(objectBuilder.build())
            .build()
            .writeTo(
                codeGenerator = codeGenerator,
                aggregating = true,
                originatingKSFiles = keys.mapNotNull { it.containingFile }.distinct(),
            )

        emitted = true
        return emptyList()
    }

    private fun KSTypeAlias.toSoilKey(): SoilKey? {
        val expanded = type.resolve()
        val fqName = expanded.declaration.qualifiedName?.asString() ?: return null
        val kind = SoilKeyKind.entries.firstOrNull { it.keyFqName == fqName } ?: return null
        val typeArguments = expanded.arguments.map { it.toTypeName() }
        val propertyName = simpleName.asString()
            .removeSuffix("Key")
            .replaceFirstChar { it.lowercaseChar() }
        return SoilKey(
            kind = kind,
            propertyName = propertyName,
            namespace = qualifiedName?.asString() ?: simpleName.asString(),
            typeArguments = typeArguments,
            packageName = packageName.asString(),
            containingFile = containingFile,
        )
    }

    private fun commonPackagePrefix(packages: List<String>): String {
        val segmentLists = packages.map { it.split('.') }
        val first = segmentLists.first()
        var prefixLength = first.size
        for (segments in segmentLists) {
            var index = 0
            while (index < prefixLength && index < segments.size && segments[index] == first[index]) index++
            prefixLength = index
        }
        return first.take(prefixLength).joinToString(".")
    }

    private data class SoilKey(
        val kind: SoilKeyKind,
        val propertyName: String,
        val namespace: String,
        val typeArguments: List<TypeName>,
        val packageName: String,
        val containingFile: com.google.devtools.ksp.symbol.KSFile?,
    )

    private enum class SoilKeyKind(val keyFqName: String, val idClass: ClassName) {
        QUERY("soil.query.QueryKey", ClassName("soil.query", "QueryId")),
        SUBSCRIPTION("soil.query.SubscriptionKey", ClassName("soil.query", "SubscriptionId")),
        MUTATION("soil.query.MutationKey", ClassName("soil.query", "MutationId")),
    }

    private companion object {
        val MUTATION_TAG = ClassName(
            "io.github.droidkaigi.confsched.core.model",
            "MutationTag",
        )
    }
}
