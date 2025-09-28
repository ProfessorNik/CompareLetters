package com.github.professornik.compareletters

import com.github.professornik.compareletters.dao.ComparedTextsRepository
import com.github.professornik.compareletters.dao.common.ConnectionProvider
import com.github.professornik.compareletters.dao.common.DbMigrator
import com.github.professornik.compareletters.service.CompareLettersService
import nu.pattern.OpenCV

class ApplicationContext(
    val generalConfig: GeneralConfig,
) {
    
    val connectionProvider: ConnectionProvider = ConnectionProvider(generalConfig.dbConfig)
    val migrator: DbMigrator = DbMigrator(generalConfig.dbMigrationConfig, connectionProvider)
    val comparedTextsRepository: ComparedTextsRepository = ComparedTextsRepository(connectionProvider)
    val comparedLettersService: CompareLettersService = CompareLettersService(comparedTextsRepository, generalConfig)
    
    fun initContext() {
        migrator.migrate()
        OpenCV.loadLocally()
    }
}