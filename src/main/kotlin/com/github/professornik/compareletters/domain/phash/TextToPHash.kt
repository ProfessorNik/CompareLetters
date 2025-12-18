//package com.github.professornik.compareletters.domain.phash
//
//fun textToPHash(text: String, hashBits: Int = 256): String {
//    val embedding = embeddingExtractor.extractEmbedding(text) ?: return ""
//    return embeddingToHash(embedding, hashBits)
//}
//
//fun similarity(text1: String, text2: String): Double {
//    val hash1 = textToPHash(text1)
//    val hash2 = textToPHash(text2)
//
//    if (hash1.isEmpty() || hash2.isEmpty() || hash1.length != hash2.length) {
//        return 0.0
//    }
//
//    return calculateSimilarity(hash1, hash2)
//}
//
//fun similarityWithEmbeddings(text1: String, text2: String): EmbeddingSimilarityResult {
//    val embedding1 = embeddingExtractor.extractEmbedding(text1) ?: floatArrayOf()
//    val embedding2 = embeddingExtractor.extractEmbedding(text2) ?: floatArrayOf()
//
//    val hash1 = embeddingToHash(embedding1)
//    val hash2 = embeddingToHash(embedding2)
//
//    return EmbeddingSimilarityResult(
//        cosineSimilarity = calculateCosineSimilarity(embedding1, embedding2),
//        pHashSimilarity = calculateSimilarity(hash1, hash2),
//        hammingDistance = hammingDistance(hash1, hash2),
//        hash1 = hash1,
//        hash2 = hash2,
//        embeddingSize1 = embedding1.size,
//        embeddingSize2 = embedding2.size
//    )
//}
//
//private fun embeddingToHash(embedding: FloatArray, bits: Int = 256): String {
//    if (embedding.isEmpty()) return ""
//
//    val median = calculateMedian(embedding)
//    return embedding
//        .take(bits.coerceAtMost(embedding.size))
//        .joinToString("") { if (it > median) "1" else "0" }
//        .padEnd(bits, '0')
//}
//
//private fun calculateMedian(embedding: FloatArray): Float {
//    val sorted = embedding.sorted()
//    return if (sorted.size % 2 == 0) {
//        (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2.0f
//    } else {
//        sorted[sorted.size / 2]
//    }
//}
//
//private fun calculateSimilarity(hash1: String, hash2: String): Double {
//    val distance = hammingDistance(hash1, hash2)
//    return 1.0 - (distance.toDouble() / hash1.length)
//}
//
//private fun hammingDistance(hash1: String, hash2: String): Int {
//    require(hash1.length == hash2.length) { "Hashes must have same length" }
//    return hash1.zip(hash2).count { (a, b) -> a != b }
//}
//
//private fun calculateCosineSimilarity(embedding1: FloatArray, embedding2: FloatArray): Double {
//    if (embedding1.isEmpty() || embedding2.isEmpty() || embedding1.size != embedding2.size) {
//        return 0.0
//    }
//
//    var dotProduct = 0.0
//    var norm1 = 0.0
//    var norm2 = 0.0
//
//    for (i in embedding1.indices) {
//        dotProduct += embedding1[i] * embedding2[i]
//        norm1 += embedding1[i] * embedding1[i]
//        norm2 += embedding2[i] * embedding2[i]
//    }
//
//    return if (norm1 > 0.0 && norm2 > 0.0) {
//        dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2))
//    } else {
//        0.0
//    }
//}
//
//data class EmbeddingSimilarityResult(
//    val cosineSimilarity: Double,
//    val pHashSimilarity: Double,
//    val hammingDistance: Int,
//    val hash1: String,
//    val hash2: String,
//    val embeddingSize1: Int,
//    val embeddingSize2: Int
//) {
//    val isSimilar: Boolean get() = pHashSimilarity > 0.8
//    val isVerySimilar: Boolean get() = pHashSimilarity > 0.95
//
//    override fun toString(): String {
//        return """
//                Cosine Similarity: ${"%.4f".format(cosineSimilarity)}
//                pHash Similarity: ${"%.4f".format(pHashSimilarity)}
//                Hamming Distance: $hammingDistance/${hash1.length}
//                Embedding Sizes: $embeddingSize1, $embeddingSize2
//                Similar: $isSimilar, Very Similar: $isVerySimilar
//                Hash1: ${hash1.take(32)}...
//                Hash2: ${hash2.take(32)}...
//            """.trimIndent()
//    }
//}