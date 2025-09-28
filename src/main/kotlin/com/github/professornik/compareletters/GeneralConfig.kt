package com.github.professornik.compareletters

import com.github.professornik.compareletters.dao.common.DbConfig
import com.github.professornik.compareletters.dao.common.DbMigrationConfig
import com.github.professornik.compareletters.domain.RenderGlyphConfig

data class GeneralConfig(
    val renderGlyphConfig: RenderGlyphConfig,
    val dbConfig: DbConfig,
    val dbMigrationConfig: DbMigrationConfig,
) {

}


