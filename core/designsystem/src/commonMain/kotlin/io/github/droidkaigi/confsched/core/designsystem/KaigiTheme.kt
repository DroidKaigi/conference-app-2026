package io.github.droidkaigi.confsched.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.droidkaigi.confsched.core.designsystem.generated.resources.Res
import io.github.droidkaigi.confsched.core.designsystem.generated.resources.kaigi_mono_regular
import io.github.droidkaigi.confsched.core.designsystem.generated.resources.kaigi_sans_medium
import io.github.droidkaigi.confsched.core.designsystem.generated.resources.kaigi_sans_regular
import io.github.droidkaigi.confsched.core.model.KaigiColorScheme
import io.github.droidkaigi.confsched.core.model.KaigiFontFamily
import io.github.droidkaigi.confsched.core.model.SketchStrength
import org.jetbrains.compose.resources.Font

// Palettes come from the Figma file's `DroidKaigi 2026 Material Theme` collection.
// Tokens that collection leaves undefined keep their existing values, among them the
// call-to-action orange shared across themes as tertiary.
private val CtaOrange = Color(0xFFE04A1E)

private val MorningMist = lightColorScheme(
    primary = Color(0xFF3A4478),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC0C8E0),
    onPrimaryContainer = Color(0xFF2A3A52),
    secondary = Color(0xFF8A7040),
    onSecondary = Color(0xFFDCE1EB),
    secondaryContainer = Color(0xFFE8D8B8),
    onSecondaryContainer = Color(0xFF2A2010),
    tertiary = CtaOrange,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF8993A4),
    onTertiaryContainer = Color(0xFF222E42),
    error = Color(0xFF932427),
    background = Color(0xFFD6DCE8),
    onBackground = Color(0xFF2A3A52),
    surface = Color(0xFFD6DCE8),
    onSurface = Color(0xFF2A3A52),
    surfaceVariant = Color(0xFFCAD2E1),
    onSurfaceVariant = Color(0xFF3A4060),
    surfaceBright = Color(0xFFE8ECF4),
    surfaceDim = Color(0xFFC9CFDA),
    surfaceContainerLow = Color(0xFFE2E6F0),
    surfaceContainer = Color(0xFFCCD2E0),
    surfaceContainerHigh = Color(0xFFC0C6D6),
    inverseSurface = Color(0xFF3A4060),
    inverseOnSurface = Color(0xFFE8ECF4),
    outline = Color(0xFF808B9D),
    outlineVariant = Color(0xFF919BAC),
)

private val DeepTeal = darkColorScheme(
    primary = Color(0xFF50C8A0),
    onPrimary = Color(0xFF11353D),
    primaryContainer = Color(0xFF0E3E38),
    onPrimaryContainer = Color(0xFFF3E4BC),
    secondary = Color(0xFFE0C060),
    onSecondary = Color(0xFF153137),
    secondaryContainer = Color(0xFFE8F0EC),
    onSecondaryContainer = Color(0xFF0E2820),
    tertiary = CtaOrange,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF35545B),
    onTertiaryContainer = Color(0xFFCDD4D6),
    error = Color(0xFFFFB4A6),
    background = Color(0xFF1A3D45),
    onBackground = Color(0xFFE8C97A),
    surface = Color(0xFF1A3D45),
    onSurface = Color(0xFFE8C97A),
    surfaceVariant = Color(0xFF2A505A),
    onSurfaceVariant = Color(0xFFC4B078),
    surfaceBright = Color(0xFFE8F0EC),
    surfaceDim = Color(0xFF122B30),
    surfaceContainerLow = Color(0xFF1E4850),
    surfaceContainer = Color(0xFF22535B),
    surfaceContainerHigh = Color(0xFF265E66),
    inverseSurface = Color(0xFFB6A468),
    inverseOnSurface = Color(0xFF1A3D45),
    outline = Color(0xFF768B8F),
    outlineVariant = Color(0xFF536E74),
)

private val SakuraPlum = lightColorScheme(
    primary = Color(0xFF6A2850),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0B0C8),
    onPrimaryContainer = Color(0xFF5C2E2A),
    secondary = Color(0xFF886848),
    onSecondary = Color(0xFFF6DBD7),
    secondaryContainer = Color(0xFFE8D4C0),
    onSecondaryContainer = Color(0xFF2A1A10),
    tertiary = CtaOrange,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFB08A85),
    onTertiaryContainer = Color(0xFF4A2522),
    error = Color(0xFF9C171D),
    background = Color(0xFFF5D5D0),
    onBackground = Color(0xFF5C2E2A),
    surface = Color(0xFFF5D5D0),
    onSurface = Color(0xFF5C2E2A),
    surfaceVariant = Color(0xFFEDC6BF),
    onSurfaceVariant = Color(0xFF4A3634),
    surfaceBright = Color(0xFFFAE4E0),
    surfaceDim = Color(0xFFE6C8C4),
    surfaceContainerLow = Color(0xFFF8DDD8),
    surfaceContainer = Color(0xFFECCBC6),
    surfaceContainerHigh = Color(0xFFE4BFB8),
    inverseSurface = Color(0xFF4A3634),
    inverseOnSurface = Color(0xFFFAE4E0),
    outline = Color(0xFFA8817D),
    outlineVariant = Color(0xFFB8928E),
)

private val Terracotta = lightColorScheme(
    primary = Color(0xFF4A6038),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC0D0A8),
    onPrimaryContainer = Color(0xFF3D2418),
    secondary = Color(0xFF8A6430),
    onSecondary = Color(0xFFEBC39C),
    secondaryContainer = Color(0xFFE8D0A8),
    onSecondaryContainer = Color(0xFF2A1A08),
    tertiary = CtaOrange,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF9B7657),
    onTertiaryContainer = Color(0xFF311D13),
    error = Color(0xFF891913),
    background = Color(0xFFE8B98A),
    onBackground = Color(0xFF3D2418),
    surface = Color(0xFFE8B98A),
    onSurface = Color(0xFF2A1A10),
    surfaceVariant = Color(0xFFDEAB79),
    onSurfaceVariant = Color(0xFF4A3828),
    surfaceBright = Color(0xFFF4D4AE),
    surfaceDim = Color(0xFFDAAE82),
    surfaceContainerLow = Color(0xFFF0C89E),
    surfaceContainer = Color(0xFFDCA878),
    surfaceContainerHigh = Color(0xFFD09868),
    inverseSurface = Color(0xFF4A3828),
    inverseOnSurface = Color(0xFFF4D4AE),
    outline = Color(0xFF926F51),
    outlineVariant = Color(0xFFA47D5C),
)

private val CampfireNight = darkColorScheme(
    primary = Color(0xFFF08050),
    onPrimary = Color(0xFF601400),
    primaryContainer = Color(0xFF5A2810),
    onPrimaryContainer = Color(0xFFEFA48E),
    secondary = Color(0xFFD0A868),
    onSecondary = Color(0xFF241E1A),
    secondaryContainer = Color(0xFFF0E0D0),
    onSecondaryContainer = Color(0xFF2A1808),
    tertiary = CtaOrange,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF46403B),
    onTertiaryContainer = Color(0xFFD1CFCE),
    error = Color(0xFFFFB3AD),
    background = Color(0xFF2D2620),
    onBackground = Color(0xFFEADFD5),
    surface = Color(0xFF2D2620),
    onSurface = Color(0xFFF08050),
    surfaceVariant = Color(0xFF423830),
    onSurfaceVariant = Color(0xFFD0885E),
    surfaceBright = Color(0xFFF0E0D0),
    surfaceDim = Color(0xFF201B16),
    surfaceContainerLow = Color(0xFF352E28),
    surfaceContainer = Color(0xFF3D3630),
    surfaceContainerHigh = Color(0xFF453E38),
    inverseSurface = Color(0xFFB1401E),
    inverseOnSurface = Color(0xFFF0E0D0),
    outline = Color(0xFF817D79),
    outlineVariant = Color(0xFF625C58),
)

/** The Material palette of [this] scheme, for callers outside a composition such as widgets. */
fun KaigiColorScheme.toMaterialColorScheme(): ColorScheme = when (this) {
    KaigiColorScheme.MorningMist -> MorningMist
    KaigiColorScheme.DeepTeal -> DeepTeal
    KaigiColorScheme.SakuraPlum -> SakuraPlum
    KaigiColorScheme.Terracotta -> Terracotta
    KaigiColorScheme.CampfireNight -> CampfireNight
}

/**
 * How far every hand-drawn amplitude swings, as chosen in the settings screen.
 *
 * Un-provided it reads as [SketchStrength.Normal], the strength the app ships at, so a drawing
 * composed outside [KaigiTheme] still comes out at its own figures.
 */
val LocalSketchStrength = staticCompositionLocalOf { SketchStrength.Normal }

/** Whether decorative animations should be reduced to their static state. */
val LocalReducedMotion = staticCompositionLocalOf { false }

/** The app-wide base seed combined with each sketch element's stable seed. */
val LocalSketchBaseSeed = staticCompositionLocalOf<Int> {
    error("LocalSketchBaseSeed must be provided")
}

/**
 * Whether the scheme in force is a dark one.
 *
 * Read this rather than sampling a color: Terracotta's surface sits close enough to the midpoint
 * that a luminance threshold would flip on a small change to its value.
 */
internal val LocalSchemeIsDark = staticCompositionLocalOf { false }

/** Whether the scheme is a dark one, for callers outside a composition. */
val KaigiColorScheme.isDark: Boolean
    get() = when (this) {
        KaigiColorScheme.MorningMist -> false
        KaigiColorScheme.DeepTeal -> true
        KaigiColorScheme.SakuraPlum -> false
        KaigiColorScheme.Terracotta -> false
        KaigiColorScheme.CampfireNight -> true
    }

// Each bundled binary merges its Latin face with Noto Sans JP, because Compose offers no
// way to direct per-glyph fallback to a bundled font.
@Composable
private fun kaigiFontFamilies(): Pair<FontFamily, FontFamily> {
    val display = FontFamily(Font(Res.font.kaigi_mono_regular, FontWeight.Normal))
    val standard = FontFamily(
        Font(Res.font.kaigi_sans_regular, FontWeight.Normal),
        Font(Res.font.kaigi_sans_medium, FontWeight.Medium),
    )
    return display to standard
}

/**
 * The type set [fontFamily] installs. Only the family changes per role; sizes, line heights, and
 * letter spacing keep the Material defaults the design file records.
 *
 * Public for a control that has to set text in a font other than the one in force, such as the
 * settings screen naming each font option in the face that option selects.
 */
@Composable
fun kaigiTypography(fontFamily: KaigiFontFamily): Typography {
    val (display, standard) = kaigiFontFamilies()
    val (displayRole, textRole) = when (fontFamily) {
        KaigiFontFamily.Default -> display to standard
        KaigiFontFamily.CourierPrime -> display to display
        KaigiFontFamily.NotoSans -> standard to standard
    }
    val defaults = Typography()
    return Typography(
        displayLarge = defaults.displayLarge.copy(fontFamily = displayRole),
        displayMedium = defaults.displayMedium.copy(fontFamily = displayRole),
        displaySmall = defaults.displaySmall.copy(fontFamily = displayRole),
        headlineLarge = defaults.headlineLarge.copy(fontFamily = displayRole),
        headlineMedium = defaults.headlineMedium.copy(fontFamily = displayRole),
        headlineSmall = defaults.headlineSmall.copy(fontFamily = displayRole),
        titleLarge = defaults.titleLarge.copy(fontFamily = textRole),
        titleMedium = defaults.titleMedium.copy(fontFamily = textRole),
        titleSmall = defaults.titleSmall.copy(fontFamily = textRole),
        bodyLarge = defaults.bodyLarge.copy(fontFamily = textRole),
        bodyMedium = defaults.bodyMedium.copy(fontFamily = textRole),
        bodySmall = defaults.bodySmall.copy(fontFamily = textRole),
        labelLarge = defaults.labelLarge.copy(fontFamily = textRole),
        labelMedium = defaults.labelMedium.copy(fontFamily = textRole),
        labelSmall = defaults.labelSmall.copy(fontFamily = textRole),
    )
}

@Composable
fun KaigiTheme(
    colorScheme: KaigiColorScheme,
    fontFamily: KaigiFontFamily,
    sketchStrength: SketchStrength,
    sketchBaseSeed: Int,
    content: @Composable () -> Unit,
) {
    val reducedMotion =
        rememberCoroutineScope().coroutineContext[MotionDurationScale]?.scaleFactor == 0f
    CompositionLocalProvider(
        LocalSchemeIsDark provides colorScheme.isDark,
        LocalSketchBaseSeed provides sketchBaseSeed,
        LocalSketchStrength provides sketchStrength,
        LocalReducedMotion provides reducedMotion,
    ) {
        MaterialTheme(
            colorScheme = colorScheme.toMaterialColorScheme(),
            typography = kaigiTypography(fontFamily),
            content = content,
        )
    }
}
