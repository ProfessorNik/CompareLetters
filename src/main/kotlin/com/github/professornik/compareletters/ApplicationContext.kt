package com.github.professornik.compareletters

import com.github.professornik.compareletters.domain.compartedletters.ComparedLettersRepository
import com.github.professornik.compareletters.dao.common.ConnectionProvider
import com.github.professornik.compareletters.dao.common.DbMigrator
import com.github.professornik.compareletters.service.CompareLettersService
import nu.pattern.OpenCV

class ApplicationContext(
    val generalConfig: GeneralConfig,
) {
    
    val connectionProvider: ConnectionProvider = ConnectionProvider(generalConfig.dbConfig)
    val migrator: DbMigrator = DbMigrator(generalConfig.dbMigrationConfig, connectionProvider)
    val comparedLettersRepository: ComparedLettersRepository = ComparedLettersRepository(connectionProvider)
    val comparedLettersService: CompareLettersService = CompareLettersService(comparedLettersRepository, generalConfig)
    
    fun initContext() {
        migrator.migrate()
        OpenCV.loadLocally()
    }
}