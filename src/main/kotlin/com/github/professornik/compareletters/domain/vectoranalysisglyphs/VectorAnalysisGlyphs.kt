package com.github.professornik.compareletters.domain.vectoranalysisglyphs

import com.github.professornik.compareletters.domain.RenderGlyphConfig
import com.github.professornik.compareletters.domain.font
import java.awt.Font
import java.awt.font.FontRenderContext
import java.awt.font.GlyphVector
import java.awt.geom.AffineTransform
import java.awt.geom.PathIterator
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

private val fontRenderContext = FontRenderContext(AffineTransform(), true, true)

data class GlyphAnalysisResult(
    val points: List<Point2D.Double>,
    val contours: List<List<Point2D.Double>>,
    val bounds: Rectangle2D,
    val metrics: GlyphMetrics
)

data class GlyphMetrics(
    val width: Double,
    val height: Double,
    val advance: Double,
    val pointsCount: Int
)

/**
 * Сравнивает композицию символов с целевым символом
 */
fun vectorAnalysisGlyphs(
    target: String,
    composition: String,
    renderGlyphConfig: RenderGlyphConfig,
    threshold: Double = 0.8
): ComparisonResult {
    val font = with(renderGlyphConfig) { font(fontSize, fontName) }
    // Анализируем композицию
    val compositeGlyph = analyzeCompositeGlyph(composition, font)
    val targetGlyph = analyzeGlyph(target, font)

    val similarity = compareGlyphs(compositeGlyph, targetGlyph)

    return ComparisonResult(
        composition = composition,
        target = target,
        similarity = similarity,
        isSuspicious = similarity >= threshold
    )
}

data class ComparisonWeights(
    val aspectWeight: Double = 0.25,
    val densityWeight: Double = 0.20,
    val bboxWeight: Double = 0.15,
    val shapeWeight: Double = 0.40
)

/**
 * Сравнивает два глифа
 */
private fun compareGlyphs(
    glyph1: GlyphAnalysisResult,
    glyph2: GlyphAnalysisResult,
    weights: ComparisonWeights = ComparisonWeights()
): Double {
    return with(weights) {
        aspectWeight * compareAspectRatios(glyph1, glyph2) +
                densityWeight * comparePointDensity(glyph1, glyph2) +
                bboxWeight * compareBoundingBoxes(glyph1, glyph2)
//                shapeWeight * compareShapes(glyph1, glyph2)
    }
}

/**
 * Анализирует глиф символа
 */
private fun analyzeGlyph(
    character: String,
    font: Font,
): GlyphAnalysisResult {
    val glyphVector: GlyphVector = font.createGlyphVector(fontRenderContext, character)
    val shape = glyphVector.getGlyphOutline(0)

    val (points, contours) = extractPointsAndContours(shape)
    val bounds = shape.bounds2D
    val metrics = calculateGlyphMetrics(glyphVector, points.size)

    return GlyphAnalysisResult(points, contours, bounds, metrics)
}

data class ComparisonResult(
    val composition: String,
    val target: String,
    val similarity: Double,
    val isSuspicious: Boolean
)


// Приватные методы реализации
private fun extractPointsAndContours(shape: java.awt.Shape): Pair<List<Point2D.Double>, List<List<Point2D.Double>>> {
    val allPoints = mutableListOf<Point2D.Double>()
    val contours = mutableListOf<List<Point2D.Double>>()
    var currentContour = mutableListOf<Point2D.Double>()

    val iterator = shape.getPathIterator(null)
    val coords = DoubleArray(6)

    while (!iterator.isDone) {
        when (iterator.currentSegment(coords)) {
            PathIterator.SEG_MOVETO -> {
                if (currentContour.isNotEmpty()) {
                    contours.add(currentContour)
                    currentContour = mutableListOf()
                }
                currentContour.add(Point2D.Double(coords[0], coords[1]))
                allPoints.add(Point2D.Double(coords[0], coords[1]))
            }

            PathIterator.SEG_LINETO -> {
                currentContour.add(Point2D.Double(coords[0], coords[1]))
                allPoints.add(Point2D.Double(coords[0], coords[1]))
            }

            PathIterator.SEG_QUADTO -> {
                val lastPoint = currentContour.lastOrNull() ?: Point2D.Double(0.0, 0.0)
                val approximated = approximateQuadraticCurve(
                    lastPoint,
                    Point2D.Double(coords[0], coords[1]),
                    Point2D.Double(coords[2], coords[3]),
                    8
                )
                currentContour.addAll(approximated)
                allPoints.addAll(approximated)
            }

            PathIterator.SEG_CUBICTO -> {
                val lastPoint = currentContour.lastOrNull() ?: Point2D.Double(0.0, 0.0)
                val approximated = approximateCubicCurve(
                    lastPoint,
                    Point2D.Double(coords[0], coords[1]),
                    Point2D.Double(coords[2], coords[3]),
                    Point2D.Double(coords[4], coords[5]),
                    8
                )
                currentContour.addAll(approximated)
                allPoints.addAll(approximated)
            }

            PathIterator.SEG_CLOSE -> {
                if (currentContour.isNotEmpty()) {
                    contours.add(currentContour)
                    currentContour = mutableListOf()
                }
            }
        }
        iterator.next()
    }

    if (currentContour.isNotEmpty()) {
        contours.add(currentContour)
    }

    return Pair(allPoints, contours)
}

private fun analyzeCompositeGlyph(text: String, font: Font): GlyphAnalysisResult {
    val glyphVector: GlyphVector = font.createGlyphVector(fontRenderContext, text)
    val shape = glyphVector.getGlyphOutline(0) // Получаем всю композицию как один глиф

    val (points, contours) = extractPointsAndContours(shape)
    val bounds = shape.bounds2D
    val metrics = calculateGlyphMetrics(glyphVector, points.size)

    return GlyphAnalysisResult(points, contours, bounds, metrics)
}

private fun calculateGlyphMetrics(glyphVector: GlyphVector, pointsCount: Int): GlyphMetrics {
    val visualBounds = glyphVector.visualBounds
    val logicalBounds = glyphVector.logicalBounds

    return GlyphMetrics(
        width = visualBounds.width,
        height = visualBounds.height,
        advance = logicalBounds.width,
        pointsCount = pointsCount
    )
}

private fun compareAspectRatios(glyph1: GlyphAnalysisResult, glyph2: GlyphAnalysisResult): Double {
    val aspect1 = glyph1.bounds.width / max(glyph1.bounds.height, 1.0)
    val aspect2 = glyph2.bounds.width / max(glyph2.bounds.height, 1.0)
    val diff = abs(aspect1 - aspect2)
    return 1.0 - (diff / max(aspect1, aspect2))
}

private fun comparePointDensity(glyph1: GlyphAnalysisResult, glyph2: GlyphAnalysisResult): Double {
    val area1 = glyph1.bounds.width * glyph1.bounds.height
    val area2 = glyph2.bounds.width * glyph2.bounds.height
    val density1 = glyph1.metrics.pointsCount / max(area1, 1.0)
    val density2 = glyph2.metrics.pointsCount / max(area2, 1.0)
    val diff = abs(density1 - density2)
    return 1.0 - (diff / max(density1, density2))
}

private fun compareBoundingBoxes(glyph1: GlyphAnalysisResult, glyph2: GlyphAnalysisResult): Double {
    val intersection = glyph1.bounds.createIntersection(glyph2.bounds)
    val union = glyph1.bounds.createUnion(glyph2.bounds)
    return intersection.width * intersection.height / (union.width * union.height)
}

private fun compareShapes(glyph1: GlyphAnalysisResult, glyph2: GlyphAnalysisResult): Double {
    val normalized1 = normalizeGlyph(glyph1)
    val normalized2 = normalizeGlyph(glyph2)

    return calculateShapeSimilarity(normalized1, normalized2)
}

private fun normalizeGlyph(glyph: GlyphAnalysisResult): GlyphAnalysisResult {
    if (glyph.bounds.width == 0.0 || glyph.bounds.height == 0.0) return glyph

    val normalizedPoints = glyph.points.map { point ->
        Point2D.Double(
            (point.x - glyph.bounds.x) / glyph.bounds.width,
            (point.y - glyph.bounds.y) / glyph.bounds.height
        )
    }

    val normalizedBounds = Rectangle2D.Double(0.0, 0.0, 1.0, 1.0)

    return glyph.copy(
        points = normalizedPoints,
        bounds = normalizedBounds
    )
}

private fun calculateShapeSimilarity(glyph1: GlyphAnalysisResult, glyph2: GlyphAnalysisResult): Double {
    // Упрощенный алгоритм сравнения форм
    val sampleCount = 100
    val samples1 = samplePoints(glyph1.points, sampleCount)
    val samples2 = samplePoints(glyph2.points, sampleCount)

    var totalDistance = 0.0
    for (i in 0 until sampleCount) {
        val dx = samples1[i].x - samples2[i].x
        val dy = samples1[i].y - samples2[i].y
        totalDistance += sqrt(dx * dx + dy * dy)
    }

    val avgDistance = totalDistance / sampleCount
    return max(0.0, 1.0 - avgDistance)
}

private fun samplePoints(points: List<Point2D.Double>, count: Int): List<Point2D.Double> {
    if (points.size <= count) return points

    return List(count) { i ->
        val index = (i * points.size) / count
        points[index]
    }
}

private fun approximateQuadraticCurve(
    p0: Point2D.Double,
    p1: Point2D.Double,
    p2: Point2D.Double,
    steps: Int
): List<Point2D.Double> {
    return (1..steps).map { step ->
        val t = step.toDouble() / steps
        val x = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x
        val y = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y
        Point2D.Double(x, y)
    }
}

private fun approximateCubicCurve(
    p0: Point2D.Double,
    p1: Point2D.Double,
    p2: Point2D.Double,
    p3: Point2D.Double,
    steps: Int
): List<Point2D.Double> {
    return (1..steps).map { step ->
        val t = step.toDouble() / steps
        val x = (1 - t) * (1 - t) * (1 - t) * p0.x +
                3 * (1 - t) * (1 - t) * t * p1.x +
                3 * (1 - t) * t * t * p2.x +
                t * t * t * p3.x
        val y = (1 - t) * (1 - t) * (1 - t) * p0.y +
                3 * (1 - t) * (1 - t) * t * p1.y +
                3 * (1 - t) * t * t * p2.y +
                t * t * t * p3.y
        Point2D.Double(x, y)
    }
}
