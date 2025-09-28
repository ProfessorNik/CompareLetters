package com.github.professornik.compareletters.dao.common

import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

class DbMigrator(
    private val dbMigrationConfig: DbMigrationConfig,
    private val connectionProvider: ConnectionProvider,
) {

    fun migrate() = connectionProvider.connection.use { connection ->
        val database = DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(JdbcConnection(connection))

        val liquibase = Liquibase(
            dbMigrationConfig.changeLogFile,
            ClassLoaderResourceAccessor(),
            database,
        )

        liquibase.update()
    }
}

data class DbMigrationConfig(
    val changeLogFile: String,
)
