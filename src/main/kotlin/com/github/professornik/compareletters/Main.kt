package com.github.professornik.compareletters

import com.github.professornik.compareletters.dao.common.DbConfig
import com.github.professornik.compareletters.dao.common.DbMigrationConfig
import com.github.professornik.compareletters.domain.FontName
import com.github.professornik.compareletters.domain.RenderGlyphConfig
import com.github.professornik.compareletters.domain.lettersiter.lettersBatches
import com.github.professornik.compareletters.domain.lettersiter.toViewString
import kotlinx.coroutines.flow.*

suspend fun main() {
    val applicationContext = ApplicationContext(config)
    applicationContext.initContext()
    processLettersBatches(applicationContext)
}

private suspend fun processLettersBatches(applicationContext: ApplicationContext) {
    lettersBatches(batchSize = 10)
        .asFlow()
        .onStart { println("Начало обработки батчей") }
        .onEach { batch ->
            println("Обработка батча: ${batch.toViewString()}")
            applicationContext.comparedLettersService.compareLetters(batch)
        }
        .onCompletion { println("Обработка батчей завершена") }
        .catch { e -> println("Ошибка: ${e.message}"); e.printStackTrace() }
        .collect()
}

private val config: GeneralConfig = GeneralConfig(
    renderGlyphConfig = RenderGlyphConfig(
        fontSize = 100F,
        fontName = FontName.ARIAL,
        width = 200,
        height = 200,
    ),
    dbConfig = DbConfig(
        jdbcUrl = "jdbc:postgresql://localhost:5432/compare_letters",
        username = "postgres",
        password = "admin",
    ),
    dbMigrationConfig = DbMigrationConfig(
        changeLogFile = "/db/changelog/changelog.xml"
    )
)