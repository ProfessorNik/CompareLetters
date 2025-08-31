package com.github.professornik

import nu.pattern.OpenCV
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

val uppercaseLetters = ('A'..'Z').toList()  // A, B, C, ..., Z
val lowercaseLetters = ('a'..'z').toList()  // a, b, c, ..., z

val letters = uppercaseLetters + lowercaseLetters

val allPairs = listOf(
    "р" to "q",
    "ф" to "cp",
    "П" to "ГI",
    "F" to "Г-",
    "m" to "rn",
    "Ш" to "LLI",
    "A" to "гр"
)

fun main() {
    OpenCV.loadLocally();

    val pairs = allPairs

    pairs.forEach {
        println("${it.first} ${it.second} Сравнение контуров=${compareLetters(it.first, it.second)}")
        ImageIO.write(renderGlyph(it.first), "PNG", File("./images/${it.first}.png"));
        ImageIO.write(renderGlyph(it.second), "PNG", File("./images/${it.second}.png"));
    }
}

fun unicodeSequence(): Sequence<String> = sequence {
    for (codePoint in 0..0x10FFFF) {
        if (Character.isValidCodePoint(codePoint)) {
            val chars = Character.toChars(codePoint)
            yield(String(chars))
        }
    }
}



fun renderGlyph(
    text: String,
    font: Font = Font("Arial", Font.PLAIN, 100),
    width: Int = 200,
    height: Int = 200
): BufferedImage {
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g2d = image.createGraphics()

    // Настройка рендеринга
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.color = Color.WHITE
    g2d.fillRect(0, 0, width, height) // Белый фон
    g2d.color = Color.BLACK
    g2d.font = font

    // Определение положения текста по центру
    val metrics = g2d.fontMetrics
    val x: Int = (width - metrics.stringWidth(text)) / 2
    val y = ((height - metrics.height) / 2) + metrics.ascent


    // Рендеринг текста
    g2d.drawString(text, x, y)
    g2d.dispose()
    return image
}


