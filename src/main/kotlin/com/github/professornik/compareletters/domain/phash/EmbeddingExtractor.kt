//package com.github.professornik.compareletters.domain.phash
//
//enum class ModelType {
//    BERT, UNIVERSAL_SENTENCE, MOBILE_BERT
//}
//
//fun extractEmbedding(
//    text: String,
//    modelType: ModelType = ModelType.UNIVERSAL_SENTENCE
//): FloatArray? {
//    return when (modelType) {
//        ModelType.BERT -> extractWithBERT(text)
//        ModelType.UNIVERSAL_SENTENCE -> extractWithUniversalSentence(text)
//        ModelType.MOBILE_BERT -> extractWithMobileBERT(text)
//    }
//}
//
//private fun extractWithBERT(text: String): FloatArray? {
//    // Используем BERT для извлечения контекстных эмбеддингов
//    return try {
//        // Здесь будет реализация с BERT моделью
//        extractWithTFLiteModel(text, "bert_embedding.tflite")
//    } catch (e: Exception) {
//        e.printStackTrace()
//        null
//    }
//}
//
//private fun extractWithUniversalSentence(text: String): FloatArray? {
//    // Universal Sentence Encoder - специализирован для предложений
//    return try {
//        extractWithTFLiteModel(text, "universal_sentence_encoder.tflite")
//    } catch (e: Exception) {
//        e.printStackTrace()
//        // Fallback: простой эмбеддинг на основе TF-IDF like features
//        createSimpleTextEmbedding(text)
//    }
//}
//
//private fun extractWithMobileBERT(text: String): FloatArray? {
//    // Облегченная версия BERT для мобильных устройств
//    return try {
//        extractWithTFLiteModel(text, "mobile_bert_embedding.tflite")
//    } catch (e: Exception) {
//        e.printStackTrace()
//        null
//    }
//}
//
//private fun extractWithTFLiteModel(text: String, modelPath: String): FloatArray? {
//    // Реализация через TFLite Interpreter
//    val interpreter = loadTFLiteModel(modelPath)
//    return interpreter?.let {
//        processTextWithModel(it, text)
//    }
//}
//
//private fun createSimpleTextEmbedding(text: String): FloatArray {
//    // Простой эмбеддинг на основе характеристик текста (fallback)
//    return FloatArray(50) { index ->
//        when (index) {
//            0 -> text.length.toFloat() / 100.0f // Нормализованная длина
//            1 -> text.wordCount().toFloat() / 20.0f // Количество слов
//            2 -> text.averageWordLength() // Средняя длина слова
//            3 -> text.uniqueWordRatio() // Уникальность слов
//            else -> text.characterDistribution(index) // Распределение символов
//        }
//    }
//}