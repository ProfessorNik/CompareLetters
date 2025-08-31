package com.github.professornik.attempts

import org.opencv.core.*
import org.opencv.highgui.HighGui
import org.opencv.imgproc.Imgproc
import com.github.professornik.renderGlyph
import kotlin.math.abs

fun compareLettersAdvanced(text1: String, text2: String): Double {
    // 1. Рендеринг и подготовка изображений
    val imgColor1 = renderGlyph(text1).toMat()
    val imgColor2 = renderGlyph(text2).toMat()

    if (imgColor1.empty() || imgColor2.empty()) {
        throw IllegalStateException("Не удалось загрузить одно из изображений.")
    }

    // 2. Преобразование в grayscale и бинаризация
    val img1 = Mat()
    val img2 = Mat()
    Imgproc.cvtColor(imgColor1, img1, Imgproc.COLOR_BGR2GRAY)
    Imgproc.cvtColor(imgColor2, img2, Imgproc.COLOR_BGR2GRAY)


    val threshold1 = Mat()
    val threshold2 = Mat()
    Imgproc.threshold(img1, threshold1, 0.0, 255.0, Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)
    Imgproc.threshold(img2, threshold2, 0.0, 255.0, Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)

    // 3. Анализ контуров
    val (contours1, hierarchy1) = findContours(threshold1)
        .apply {
            showImage(threshold1, upgradeShowingImage = {
                drawContours(it, contours)
            })
        }
        .updateContours { combineContours(it) }
    val (contours2, hierarchy2) = findContours(threshold2)
        .apply {
            showImage(threshold2, upgradeShowingImage = {
                drawContours(it, contours)
            })
        }
        .updateContours { combineContours(it) }

    // Сравнение контуров
    val contourMatch = Imgproc.matchShapes(contours1[0], contours2[0], Imgproc.CONTOURS_MATCH_I1, 0.0)

    // 5. Анализ линий (дополнительно, для структурного сравнения)
    val lines1 = Mat()
    val lines2 = Mat()
    Imgproc.HoughLinesP(
        threshold1, lines1,
        1.0, Math.PI / 180,
        100,  // увеличенный порог
        50.0, // мин. длина линии
        5.0   // макс. разрыв
    )
    Imgproc.HoughLinesP(
        threshold2, lines2,
        1.0, Math.PI / 180,
        100, 50.0, 5.0
    )

    // Анализ ориентации линий
    val (vertical1, horizontal1) = analyzeLineOrientations(lines1)
    val (vertical2, horizontal2) = analyzeLineOrientations(lines2)
    val lineOrientationDiff = abs(vertical1 - vertical2) + abs(horizontal1 - horizontal2)

    // 6. Комбинированная оценка схожести
    val totalScore = contourMatch

//    println("Результаты сравнения '$text1' и '$text2':")
//    println("- Сходство контуров: ${"%.4f".format(contourMatch)} (чем ближе к 0, тем лучше)")
//    println("- Разница в ориентации линий: $lineOrientationDiff")
//    println("- Итоговый показатель различия: ${"%.4f".format(totalScore)}")

    return totalScore
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

data class ContoursWithHierarchy(
    val contours: List<MatOfPoint>,
    val hierarchy: Mat,
) {
    fun updateContours(update: (contours: List<MatOfPoint>) -> List<MatOfPoint>): ContoursWithHierarchy {
        return this.copy(contours = update(contours))
    }
}

private fun analyzeLineOrientations(lines: Mat): Pair<Int, Int> {
    var verticalLines = 0
    var horizontalLines = 0

    for (i in 0 until lines.rows()) {
        val line = lines.get(i, 0)
        val x1 = line[0]
        val y1 = line[1]
        val x2 = line[2]
        val y2 = line[3]

        // Вертикальные линии (угол ~90 градусов)
        if (abs(x1 - x2) < 5) verticalLines++
        // Горизонтальные линии (угол ~0 градусов)
        else if (abs(y1 - y2) < 5) horizontalLines++
    }

    return Pair(verticalLines, horizontalLines)
}


fun showImage(image: Mat, upgradeShowingImage: (Mat) -> Unit = {}, windowName: String = "Image") {
    // Если изображение одноканальное (GRAYSCALE), преобразуем в BGR
    val displayingImage = if (image.channels() == 1) {
        val tmp = Mat()
        Imgproc.cvtColor(image, tmp, Imgproc.COLOR_GRAY2BGR)
        tmp
    } else {
        image
    }

    upgradeShowingImage(displayingImage)

    HighGui.imshow(windowName, displayingImage)
    HighGui.waitKey(0) // 0 - ждёт нажатия любой клавиши
    HighGui.destroyWindow(windowName)
}

fun drawContours(mat: Mat, contours: List<MatOfPoint>) {
    Imgproc.drawContours(
        mat,
        contours,
        -1, // индекс контура (-1 означает все контуры)
        Scalar(0.0, 255.0, 0.0), // цвет (зеленый)
        2 // толщина линии
    )
}

fun combineContours(contours: List<MatOfPoint>): List<MatOfPoint> {
    val combinedPoints = contours.flatMap { it.toList() }
    return listOf(MatOfPoint(*combinedPoints.toTypedArray()))
}