package com.github.professornik.compareletters.dao.common

import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import javax.sql.DataSource

class ConnectionProvider(dbConfig: DbConfig) {

    val dataSource: DataSource = HikariDataSource(toHikariConfig(dbConfig))
    val connection: Connection = dataSource.connection

    fun <T> transaction(block: (Connection) -> T): T = connection.use { connection ->
        try {
            connection.autoCommit = false
            val result = block(connection)
            connection.commit()
            return result
        } catch (ex: Exception) {
            if (!connection.isClosed && !connection.autoCommit) {
                connection.rollback()
            }
            throw ex
        } finally {
            connection.autoCommit = true
        }
    }
}

