package com.github.professornik.compareletters.service

import com.github.professornik.compareletters.GeneralConfig
import com.github.professornik.compareletters.dao.ComparedTextsRepository
import com.github.professornik.compareletters.domain.compareTexts

class CompareLettersService(
    private val comparedTextsRepository: ComparedTextsRepository,
    private val generalConfig: GeneralConfig
) {

    fun compareLetters() {
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
            "Ш" to "XYZ",
            "A" to "гр"
        )

        val pairs = allPairs

        pairs.forEach {
            println("${it.first} ${it.second} Сравнение контуров=${compareTexts(text1 = it.first, text2 = it.second, config = generalConfig)}")
//        ImageIO.write(renderGlyph(it.first, config.renderGlyphConfig), "PNG", File("./images/${it.first}.png"));
//        ImageIO.write(renderGlyph(it.second, config.renderGlyphConfig), "PNG", File("./images/${it.second}.png"));
        }
    }

    private fun unicodeSequence(): Sequence<String> = sequence {
        for (codePoint in 0..0x10FFFF) {
            if (Character.isValidCodePoint(codePoint)) {
                val chars = Character.toChars(codePoint)
                yield(String(chars))
            }
        }
    }
}