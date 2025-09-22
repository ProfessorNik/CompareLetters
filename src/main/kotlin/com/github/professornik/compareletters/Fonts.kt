package com.github.professornik.compareletters

import java.awt.Font

typealias GetFont = (fontSize: Float) -> Font

fun font(fontSize: Float, fontName: FontName) : Font {
    val getFont = when (fontName) {
        FontName.ROBOTO_REGULAR -> robotoRegular
        FontName.ARIAL -> arial
    }

    return getFont(fontSize)
}


enum class FontName {
    ROBOTO_REGULAR,
    ARIAL,
}

val robotoRegular : GetFont = { fontSize ->
    (object {}::class as Any).javaClass.getResourceAsStream("/Roboto-Regular.ttf").use { fontFile ->
        Font.createFont(Font.TRUETYPE_FONT, fontFile)
            .deriveFont(fontSize)
    }
}

val arial : GetFont = { fontSize ->
    Font("Arial", Font.PLAIN, fontSize.toInt())
}