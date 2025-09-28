package com.github.professornik.compareletters.dao

import com.github.professornik.compareletters.domain.ComparedTexts
import com.github.professornik.compareletters.dao.common.ConnectionProvider

class ComparedTextsRepository(
    val connectionProvider: ConnectionProvider,
) {

    fun save(comparedTexts: ComparedTexts) = connectionProvider.transaction { connection ->

    }
}