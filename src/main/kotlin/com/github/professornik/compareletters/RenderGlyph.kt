package com.github.professornik.compareletters

import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage

data class RenderGlyphConfig(
    val fontSize: Float = 16F,
    val fontName: FontName = FontName.ROBOTO_REGULAR,
    val width: Int = 32,
    val height: Int = 32
)

fun renderGlyph(
    text: String,
    renderGlyphConfig: RenderGlyphConfig,
) = with(renderGlyphConfig) {
    renderGlyph(
        text,
        font(fontSize, fontName),
        width,
        height,
    )
}

fun renderGlyph(
    text: String,
    font: Font = font(16F, FontName.ROBOTO_REGULAR),
    width: Int = 32,
    height: Int = 32
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
