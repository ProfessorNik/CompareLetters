package com.github.professornik.compareletters.domain.compartedletters

import com.github.professornik.compareletters.dao.common.ConnectionProvider
import java.sql.Connection

class ComparedLettersRepository(
    val connectionProvider: ConnectionProvider,
) {

    fun save(comparedLetters: ComparedLetters) = connectionProvider.transaction { connection: Connection ->

    }

    fun saveBatch(comparedLetters: List<ComparedLetters>) {
        if (comparedLetters.isEmpty()) return

        connectionProvider.transaction { connection ->
            val sql = """
            INSERT INTO compared_letters (text1, text2, hu_invariants)
            VALUES (?, ?, ?)
            ON CONFLICT (text1, text2) 
            DO UPDATE SET hu_invariants = EXCLUDED.hu_invariants
        """.trimIndent()

            connection.prepareStatement(sql).use { statement ->
                for (item in comparedLetters) {
                    statement.setString(1, item.text1)
                    statement.setString(2, item.text2)
                    item.huInvariants?.let {
                        statement.setDouble(3, it)
                    } ?: statement.setNull(3, java.sql.Types.DOUBLE)
                    statement.addBatch()
                }

                val results = statement.executeBatch()
                println("Processed ${results.size} records")
            }
        }
    }
}