package com.github.professornik.compareletters

import java.awt.Font

fun robotoRegular(
    fontSize: Float = 16f,
): Font {
    return (object {}::class as Any).javaClass.getResourceAsStream("/Roboto-Regular.ttf").use { fontFile ->
        Font.createFont(Font.TRUETYPE_FONT, fontFile)
            .deriveFont(fontSize)
    }
}

fun arial(): Font {
    return Font("Arial", Font.PLAIN, 100)
}