package com.github.professornik.compareletters

import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage

fun renderGlyph(
    text: String,
    font: Font = robotoRegular(),
    width: Int = 30,
    height: Int = 30
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
