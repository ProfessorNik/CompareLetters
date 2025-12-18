package com.github.professornik.compareletters.service

import com.github.professornik.compareletters.GeneralConfig
import com.github.professornik.compareletters.domain.compartedletters.ComparedLetters
import com.github.professornik.compareletters.domain.compartedletters.ComparedLettersRepository
import com.github.professornik.compareletters.domain.humoments.huCompareLetters
import com.github.professornik.compareletters.domain.lettersiter.LettersBatch
import com.github.professornik.compareletters.domain.vectoranalysisglyphs.vectorAnalysisGlyphs

class CompareLettersService(
    private val comparedLettersRepository: ComparedLettersRepository,
    private val generalConfig: GeneralConfig
) {

    fun compareLetters(lettersBatch: LettersBatch) {
        val resultBatch = lettersBatch.map {
            huCompareLetters(
                text1 = it.reference,
                text2 = it.compared,
                config = generalConfig,
            )
        }.map {
            ComparedLetters(
                text1 = it.text1,
                text2 = it.text2,
                huInvariants = it.huInvariants
            )
        }

        comparedLettersRepository.saveBatch(resultBatch)
    }

    fun testCompare() {
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

        Character.getType('a')

        pairs.forEach {
            println(
                "${it.first} ${it.second} Сравнение контуров=${
                    huCompareLetters(
                        text1 = it.first,
                        text2 = it.second,
                        config = generalConfig
                    )
                } Векторный анализ=${vectorAnalysisGlyphs(target = it.first, composition = it.second, generalConfig.renderGlyphConfig)}"
            )
        }
    }
}