package io.github.droidkaigi.confsched.app.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.github.droidkaigi.confsched.core.model.MultiLangText

internal fun Context.localized(text: MultiLangText): String =
    if (resources.configuration.locales[0].language == "ja") text.ja else text.en

internal fun monoStyle(color: Color, size: TextUnit, weight: FontWeight): TextStyle = TextStyle(
    color = ColorProvider(color),
    fontSize = size,
    fontWeight = weight,
    fontFamily = FontFamily.Monospace,
)

internal fun sansStyle(color: Color, size: TextUnit): TextStyle = TextStyle(
    color = ColorProvider(color),
    fontSize = size,
    fontFamily = FontFamily.SansSerif,
)

internal fun sansStyle(color: Color, size: TextUnit, align: TextAlign): TextStyle = TextStyle(
    color = ColorProvider(color),
    fontSize = size,
    fontFamily = FontFamily.SansSerif,
    textAlign = align,
)
