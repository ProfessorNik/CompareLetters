package com.github.professornik.compareletters.domain.phash

fun String.wordCount(): Int = this.split("\\s+".toRegex()).size

fun String.averageWordLength(): Float {
    val words = this.split("\\s+".toRegex()).filter { it.isNotBlank() }
    return if (words.isEmpty()) 0f else words.sumOf { it.length }.toFloat() / words.size
}

fun String.uniqueWordRatio(): Float {
    val words = this.split("\\s+".toRegex()).filter { it.isNotBlank() }
    return if (words.isEmpty()) 0f else words.toSet().size.toFloat() / words.size
}

fun String.characterDistribution(index: Int): Float {
    val chars = this.lowercase().toCharArray()
    val targetChar = 'a' + (index % 26)
    return chars.count { it == targetChar }.toFloat() / this.length.coerceAtLeast(1)
}

fun String.normalizeText(): String {
    return this.lowercase()
        .replace("[^a-za-zа-яё0-9\\s]".toRegex(), " ") // Удаляем пунктуацию
        .replace("\\s+".toRegex(), " ") // Множественные пробелы в один
        .trim()
}