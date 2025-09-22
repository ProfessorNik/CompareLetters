package com.github.professornik.compareletters.dao

import com.github.professornik.compareletters.ComparedTexts
import com.github.professornik.compareletters.GeneralConfig

class ComparedTextsRepository(
    val generalConfig: GeneralConfig,
    // подключаем постгрес
) {

    fun save(comparedTexts: ComparedTexts) {

    }
}