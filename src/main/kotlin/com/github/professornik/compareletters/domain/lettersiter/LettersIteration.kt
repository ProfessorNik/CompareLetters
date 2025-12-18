package com.github.professornik.compareletters.domain.lettersiter

typealias LettersBatch = List<Letters>

fun LettersBatch.toViewString() {
    fold("") { acc, letters -> acc + letters.reference + " " + letters.compared + "  " }
}

data class Letters(
    val reference: String,
    val compared: String,
)

data class StartLettersCodePoints(
    val referenceLetter: ReferenceLetter,
    val comparedLetters: ComparedLettersCodePoints,
) {
    data class ReferenceLetter(
        val index: Int
    )

    data class ComparedLettersCodePoints(
        val firstLetter: Int,
        val secondLetter: Int,
        val thirdLetter: Int?,
    )
}

fun main() {
    lettersBatches(batchSize = 10)
        .take(10)
        .forEach { batch -> println(batch.fold("") { acc, it -> acc + it.reference + " " + it.compared + "; " }) }
}

fun lettersBatches(
    start: StartLettersCodePoints = StartLettersCodePoints(
        referenceLetter = StartLettersCodePoints.ReferenceLetter(
            index = 0
        ),
        comparedLetters = StartLettersCodePoints.ComparedLettersCodePoints(
            firstLetter = 0,
            secondLetter = 0,
            thirdLetter = null,
        )
    ),
    batchSize: Int = 1000,
): Sequence<LettersBatch> = getReferenceLetters(start.referenceLetter.index)
    .flatMap { ref ->
        val currentRefIndex = getReferenceLetterIndex(ref)
        val comparedLetters =
            if (currentRefIndex == start.referenceLetter.index) {
                val (firstLetterCodePoint, secondLetterCodePoint, thirdLetterCodePoint) = start.comparedLetters

                if (thirdLetterCodePoint == null) {
                    twoLettersCombination(firstLetterCodePoint, secondLetterCodePoint) + threeLettersCombination()
                } else {
                    threeLettersCombination(firstLetterCodePoint, secondLetterCodePoint, thirdLetterCodePoint)
                }
            } else {
                twoLettersCombination() + threeLettersCombination()
            }

        comparedLetters.map {
            Letters(
                reference = ref,
                compared = it,
            )
        }
    }
    .chunked(batchSize)

private fun twoLettersCombination(firstLetterCodePoint: Int = 0, secondLetterCodePoint: Int = 0): Sequence<String> =
    desiredLetters(firstLetterCodePoint)
        .flatMap { l1 ->
            desiredLetters(secondLetterCodePoint)
                .map { l2 ->
                    l1 + l2
                }
        }

private fun threeLettersCombination(
    firstLetterCodePoint: Int = 0,
    secondLetterCodePoint: Int = 0,
    thirdLetterCodePoint: Int = 0
): Sequence<String> =
    desiredLetters(firstLetterCodePoint)
        .flatMap { l1 ->
            desiredLetters(secondLetterCodePoint)
                .flatMap { l2 ->
                    desiredLetters(thirdLetterCodePoint)
                        .map { l3 ->
                            l1 + l2 + l3
                        }
                }
        }

fun getReferenceLetterIndex(searched: String): Int {
    return getReferenceLetters().withIndex()
        .first { (_, referenceLetter) ->
            referenceLetter == searched
        }
        .index
}

private fun getReferenceLetters(index: Int = 0): Sequence<String> = sequence {
    // Английский алфавит
    for (char in 'A'..'Z') yield(char)
    for (char in 'a'..'z') yield(char)

    // Русский алфавит (без Ё)
    for (char in 'А'..'Я') yield(char)
    for (char in 'а'..'я') yield(char)
}
    .map { it.toString() }
    .drop(index)


private fun desiredLetters(startCodePoint: Int = 0): Sequence<String> = sequence {
    for (codePoint in startCodePoint..0x10FFFF) {
        if (isDesiredCodePoint(codePoint)) {
            val chars = Character.toChars(codePoint)
            yield(String(chars))
        }
    }
}


private val isNotDesiredCharacterTypes = setOf(
    Character.CONTROL,
    Character.FORMAT,
    Character.PRIVATE_USE,
    Character.SURROGATE,
    Character.UNASSIGNED,
    Character.LINE_SEPARATOR,
    Character.PARAGRAPH_SEPARATOR,
    Character.SPACE_SEPARATOR
)

private fun isDesiredCodePoint(codePoint: Int): Boolean {
    return Character.isValidCodePoint(codePoint)
            && !isNotDesiredCharacterTypes.contains(Character.getType(codePoint).toByte())
}