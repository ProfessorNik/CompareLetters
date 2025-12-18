package com.github.professornik.compareletters.domain.compartedletters

data class ComparedLetters(
    val text1: String,
    val text2: String,
    val huInvariants: Double? = null,
)