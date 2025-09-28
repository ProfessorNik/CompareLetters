package com.github.professornik.compareletters.dao.common

import com.zaxxer.hikari.HikariConfig

data class DbConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
)

fun toHikariConfig(dbConfig: DbConfig): HikariConfig =
    HikariConfig().apply {
        jdbcUrl = dbConfig.jdbcUrl
        username = dbConfig.username
        password = dbConfig.password
    }