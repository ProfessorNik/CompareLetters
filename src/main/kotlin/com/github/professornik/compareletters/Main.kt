package com.github.professornik.compareletters

import com.github.professornik.compareletters.dao.common.DbConfig
import com.github.professornik.compareletters.dao.common.DbMigrationConfig
import com.github.professornik.compareletters.domain.FontName
import com.github.professornik.compareletters.domain.RenderGlyphConfig

fun main() {
    val applicationContext = ApplicationContext(config)
    applicationContext.initContext()
    applicationContext.comparedLettersService.compareLetters()
}

private val config: GeneralConfig = GeneralConfig(
    renderGlyphConfig = RenderGlyphConfig(
        fontSize = 100F,
        fontName = FontName.ROBOTO_REGULAR,
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