package com.github.professornik.compareletters

import nu.pattern.OpenCV
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
