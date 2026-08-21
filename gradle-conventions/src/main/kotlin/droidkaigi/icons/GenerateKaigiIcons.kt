package droidkaigi.icons

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

/**
 * Writes one `ImageVector` extension per vector drawable in the source directory.
 *
 * The drawable is the icon's only source: `android:pathData` is SVG path syntax, which
 * `addPathNodes` parses unchanged, so nothing here reshapes the geometry a re-export carries.
 */
@CacheableTask
abstract class GenerateKaigiIcons : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val iconDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val out = outputDirectory.get().asFile
        out.deleteRecursively()
        val packageDirectory = File(out, PACKAGE.replace('.', '/')).apply { mkdirs() }

        val drawables = iconDirectory.get().asFile.listFiles { file -> file.extension == "xml" }
            .orEmpty()
            .sortedBy { it.name }

        require(drawables.isNotEmpty()) { "no vector drawables in ${iconDirectory.get().asFile}" }

        for (drawable in drawables) {
            val icon = parse(drawable)
            File(packageDirectory, "${icon.name}.kt").writeText(icon.toSource())
        }
        logger.lifecycle("KaigiIcons: generated ${drawables.size} icons")
    }

    private fun parse(file: File): Icon {
        val document = DocumentBuilderFactory.newInstance()
            .apply {
                isNamespaceAware = true
                // A drawable is data; resolving a doctype or an entity from one would read the build host.
                setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
                isXIncludeAware = false
                isExpandEntityReferences = false
            }
            .newDocumentBuilder()
            .parse(file)
        val vector = document.documentElement
        val paths = vector.getElementsByTagName("path").let { nodes ->
            (0 until nodes.length).map { index ->
                val path = nodes.item(index) as Element
                val stroke = path.androidAttribute("strokeColor")
                IconPath(
                    data = requireNotNull(path.androidAttribute("pathData")) {
                        "${file.name}: a path carries no android:pathData"
                    },
                    strokeWidth = path.androidAttribute("strokeWidth").takeIf { stroke != null },
                )
            }
        }
        require(paths.isNotEmpty()) { "${file.name}: holds no path" }
        return Icon(
            name = file.nameWithoutExtension.removePrefix("hd_").toPascalCase(),
            defaultWidth = vector.androidAttribute("width")?.removeSuffix("dp") ?: "24",
            defaultHeight = vector.androidAttribute("height")?.removeSuffix("dp") ?: "24",
            viewportWidth = vector.androidAttribute("viewportWidth") ?: "24",
            viewportHeight = vector.androidAttribute("viewportHeight") ?: "24",
            autoMirrored = vector.androidAttribute("autoMirrored") == "true",
            paths = paths,
        )
    }

    private fun Element.androidAttribute(name: String): String? =
        getAttributeNS(ANDROID_NAMESPACE, name).takeIf { it.isNotEmpty() }

    private data class IconPath(val data: String, val strokeWidth: String?)

    private data class Icon(
        val name: String,
        val defaultWidth: String,
        val defaultHeight: String,
        val viewportWidth: String,
        val viewportHeight: String,
        val autoMirrored: Boolean,
        val paths: List<IconPath>,
    ) {
        fun toSource(): String = buildString {
            appendLine("// Generated from icons/. Edit the vector drawable, never this file.")
            appendLine("package $PACKAGE")
            appendLine()
            appendLine("import androidx.compose.ui.graphics.Color")
            appendLine("import androidx.compose.ui.graphics.SolidColor")
            appendLine("import androidx.compose.ui.graphics.StrokeCap")
            appendLine("import androidx.compose.ui.graphics.StrokeJoin")
            appendLine("import androidx.compose.ui.graphics.vector.ImageVector")
            appendLine("import androidx.compose.ui.graphics.vector.addPathNodes")
            appendLine("import androidx.compose.ui.unit.dp")
            appendLine()
            appendLine("val KaigiIcons.Default.$name: ImageVector")
            appendLine("    get() = cached$name ?: ImageVector.Builder(")
            appendLine("        name = \"KaigiIcons.Default.$name\",")
            appendLine("        defaultWidth = $defaultWidth.dp,")
            appendLine("        defaultHeight = $defaultHeight.dp,")
            appendLine("        viewportWidth = ${viewportWidth}f,")
            appendLine("        viewportHeight = ${viewportHeight}f,")
            if (autoMirrored) appendLine("        autoMirror = true,")
            appendLine("    )")
            for (path in paths) {
                appendLine("        .addPath(")
                appendLine("            pathData = addPathNodes(\"${path.data}\"),")
                if (path.strokeWidth != null) {
                    appendLine("            stroke = SolidColor(Color.Black),")
                    appendLine("            strokeLineWidth = ${path.strokeWidth}f,")
                    appendLine("            strokeLineCap = StrokeCap.Round,")
                    appendLine("            strokeLineJoin = StrokeJoin.Round,")
                } else {
                    appendLine("            fill = SolidColor(Color.Black),")
                }
                appendLine("        )")
            }
            appendLine("        .build()")
            appendLine("        .also { cached$name = it }")
            appendLine()
            appendLine("private var cached$name: ImageVector? = null")
        }
    }

    private companion object {
        const val PACKAGE = "io.github.droidkaigi.confsched.core.designsystem.icon"
        const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"

        fun String.toPascalCase(): String = split('_')
            .joinToString("") { part -> part.replaceFirstChar { it.uppercaseChar() } }
    }
}
