package com.github.professornik.compareletters

import nu.pattern.OpenCV
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * Сравнивает две последовательности букв. Используется метод моментов Ху для сравнения контуров букв.
 * 
 * @param text1 первая последовательность
 * @param text2 вторая последовательность
 * @return число большее 0, чем ближе к 0 тем более похожи последовательности
 * @sample sample
 */
fun compareLetters(text1: String, text2: String, cache: Cache<String, ContoursWithHierarchy> = emptyCache()): Double {
    val (contours1, _) = cache(text1, ::findContour)
    val (contours2, _) = cache(text2, ::findContour)

    // 4. Сравнение контуров методом моментов HU
    return Imgproc.matchShapes(contours1[0], contours2[0], Imgproc.CONTOURS_MATCH_I1, 0.0)
}

private fun findContour(text: String): ContoursWithHierarchy {
    // 1. Рендеринг и подготовка изображений
    val imgColor = renderGlyph(text).toMat()
    if (imgColor.empty()) {
        throw IllegalStateException("Не удалось загрузить одно из изображений.")
    }

    // 2. Преобразование в grayscale и бинаризация
    val img = Mat()
    Imgproc.cvtColor(imgColor, img, Imgproc.COLOR_BGR2GRAY)

    val threshold1 = Mat()
    Imgproc.threshold(img, threshold1, 0.0, 255.0, Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)

    // 3. Поиск контуров и их объединение в один контур
    return findContours(threshold1)
        .combineContours()
}


private fun findContours(
    image: Mat
): ContoursWithHierarchy {
    val contours = mutableListOf<MatOfPoint>()
    val hierarchy = Mat()

    Imgproc.findContours(
        image.clone(),
        contours,
        hierarchy,
        Imgproc.RETR_LIST,
        Imgproc.CHAIN_APPROX_SIMPLE
    )

    fun isIllegalContour(mat: Mat): Boolean {
        val area = Imgproc.contourArea(mat)
        val imageArea = image.rows() * image.cols()
        return area < imageArea * 0.95
    }

    return ContoursWithHierarchy(
        contours.filter(::isIllegalContour),
        hierarchy
    )
}

private class ContoursWithHierarchyMapCache : Cache<String, ContoursWithHierarchy> {
    val cache: MutableMap<String, ContoursWithHierarchy> = mutableMapOf()

    override fun get(key: String): ContoursWithHierarchy? {
        return cache[key];
    }

    override fun set(key: String, value: ContoursWithHierarchy) {
        cache.put(key, value)
    }
}

data class ContoursWithHierarchy(
    val contours: List<MatOfPoint>,
    val hierarchy: Mat,
)

private fun ContoursWithHierarchy.combineContours(): ContoursWithHierarchy {
    return this.copy(contours = combineContours(contours))
}

private fun combineContours(contours: List<MatOfPoint>): List<MatOfPoint> {
    val combinedPoints = contours.flatMap { it.toList() }
    return listOf(MatOfPoint(*combinedPoints.toTypedArray()))
}

private fun BufferedImage.toMat(): Mat {
    val stream = ByteArrayOutputStream()
    ImageIO.write(this, "jpg", stream)
    stream.flush()
    return Imgcodecs.imdecode(MatOfByte(*stream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED)
}

private fun sample() {
    OpenCV.loadLocally()

    val comparedPairs = listOf(
        "р" to "q",
        "ф" to "cp",
        "П" to "ГI",
        "F" to "Г-",
        "m" to "rn",
        "Ш" to "LLI",
        "A" to "гр"
    ).forEach {
        println("${it.first} ${it.second} Сравнение контуров=${compareLetters(it.first, it.second)}")
    }

    /**
     * Вывод
     * р q Сравнение контуров=0.017349567997407528
     * ф cp Сравнение контуров=1.9398059336569686
     * П ГI Сравнение контуров=2.7782174817415184
     * F Г- Сравнение контуров=4.99198573739263
     * m rn Сравнение контуров=0.4281385269960267
     * Ш LLI Сравнение контуров=2.476034973308238
     * A гр Сравнение контуров=11.938828761076126
     */
}